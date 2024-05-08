### 集群方式安装
docker compose安装zookeeper3.4版本
编写配置文件，并将其命名为：docker-compose.yml
配置文件内容

```text
version: '2'
services:
    zoo1:
        image: zookeeper:3.4
        restart: always
        container_name: zoo1
        ports:
            - "2181:2181"
        environment:
            ZOO_MY_ID: 1
            ZOO_SERVERS: server.1=zoo1:2888:3888 server.2=zoo2:2888:3888 server.3=zoo3:2888:3888

    zoo2:
        image: zookeeper:3.4
        restart: always
        container_name: zoo2
        ports:
            - "2182:2181"
        environment:
            ZOO_MY_ID: 2
            ZOO_SERVERS: server.1=zoo1:2888:3888 server.2=zoo2:2888:3888 server.3=zoo3:2888:3888

    zoo3:
        image: zookeeper:3.4
        restart: always
        container_name: zoo3
        ports:
            - "2183:2181"
        environment:
            ZOO_MY_ID: 3
            ZOO_SERVERS: server.1=zoo1:2888:3888 server.2=zoo2:2888:3888 server.3=zoo3:2888:3888
```

此配置文件表示，Docker需要启动三个zookeeper实例，并将2181，2182，2183三个端口号映射到容器内的2181这个端口上。
ZOO_MY_ID：表示zk服务的ID, 取值为1-255之间的整数，且必须唯一
ZOO_SERVERS：表示zk集群的主机列表

启动zookeeper集群
docker-compose up -d
该命令执行需要在docker-compose配置文件的目录下执行

查看zookeeper集群节点主从关系
使用
docker exec -it zoo1 /bin/bash
这个命令进入zoo1节点中，之后输入
./bin/zkServer.sh statu
来查看节点主从关系

### zookeeper 3.5.*版本 本地伪集群 问题 Client port not found in static config file. Looking in dynamic config f
https://github.com/31z4/zookeeper-docker/issues/65

Client port not found in static config file. 
Looking in dynamic config file. grep: : No such file or directory

zoo.cfg 版本配置区别
3.4.*和更早版本配置

# example sakes.

dataDir= /data
dataLogDir= /log
clientPort= 2187
server.1= localhost:2887:3887
server.2= localhost:2888:3888
server.3= localhost:2889:3889

------------------------------------------------

clientPort=2181
dataDir=/data
dataLogDir=/datalog
server.1=zoo1:2888:3888
server.2=zoo2:2888:3888
server.3=zoo3:2888:3888



3.5.* zoo.cfg 配置
注意点：server 配置后面 多了 “;2187” 对应clientPort

# example sakes.

dataDir= /data
dataLogDir= /log
clientPort= 2187
server.1= localhost:2887:3887;2187
server.2= localhost:2888:3888;2187
server.3= localhost:2889:3889;2187

示例
```text
version: '2'
services:
    zoo1:
        image: zookeeper:3.5.6
        restart: always
        container_name: zoo1
        ports:
            - "2181:2181"
        environment:
            ZOO_MY_ID: 1
            ZOO_SERVERS: server.1=zoo1:2888:3888;2181 server.2=zoo2:2888:3888;2181 server.3=zoo3:2888:3888;2181

    zoo2:
        image: zookeeper:3.5.6
        restart: always
        container_name: zoo2
        ports:
            - "2182:2181"
        environment:
            ZOO_MY_ID: 2
            ZOO_SERVERS: server.1=zoo1:2888:3888;2181 server.2=zoo2:2888:3888;2181 server.3=zoo3:2888:3888;2181

    zoo3:
        image: zookeeper:3.5.6
        restart: always
        container_name: zoo3
        ports:
            - "2183:2181"
        environment:
            ZOO_MY_ID: 3
            ZOO_SERVERS: server.1=zoo1:2888:3888;2181 server.2=zoo2:2888:3888;2181 server.3=zoo3:2888:3888;2181
```

### 官方文档示例
https://hub.docker.com/_/zookeeper
Example docker-compose.yml for zookeeper
基于最新版本，截止目前2024-05-07，已经超过3.5.*版本
```text
version: '3.1'

services:
  zoo1:
    image: zookeeper
    restart: always
    hostname: zoo1
    ports:
      - 2181:2181
    environment:
      ZOO_MY_ID: 1
      ZOO_SERVERS: server.1=zoo1:2888:3888;2181 server.2=zoo2:2888:3888;2181 server.3=zoo3:2888:3888;2181

  zoo2:
    image: zookeeper
    restart: always
    hostname: zoo2
    ports:
      - 2182:2181
    environment:
      ZOO_MY_ID: 2
      ZOO_SERVERS: server.1=zoo1:2888:3888;2181 server.2=zoo2:2888:3888;2181 server.3=zoo3:2888:3888;2181

  zoo3:
    image: zookeeper
    restart: always
    hostname: zoo3
    ports:
      - 2183:2181
    environment:
      ZOO_MY_ID: 3
      ZOO_SERVERS: server.1=zoo1:2888:3888;2181 server.2=zoo2:2888:3888;2181 server.3=zoo3:2888:3888;2181
```


### zookeeper创建有序节点，序号是否会被用完？
zookeeper，并在/lock下创建临时的且有序的子节点，第一个客户端对应的子节点为/lock/lock-0000000000，第二个为/lock/lock-0000000001，以此类推。
这个节点序号是否会被用完？
他是根据父节点cversion字段来的 不足10位补0
这个cversion是个integer类型,最大值2147483647,超过先变负,再超过就变正, 轮回
0000000001->0000000002->...->2147483647->-2147483648->-2147483647->...->0000000001

ZooKeeper 节点是有生命周期的，这取决于节点的类型。在 ZooKeeper 中，节点类型可以分为持久节点（PERSISTENT ）、临时节点（EPHEMERAL），以及时序节点（SEQUENTIAL ），具体在节点创建过程中，一般是组合使用，可以生成以下 4 种节点类型。
持久节点（PERSISTENT）
所谓持久节点，是指在节点创建后，就一直存在，直到有删除操作来主动清除这个节点——不会因为创建该节点的客户端会话失效而消失。
持久顺序节点（PERSISTENT_SEQUENTIAL）
这类节点的基本特性和上面的节点类型是一致的。额外的特性是，在ZK中，每个父节点会为他的第一级子节点维护一份时序，会记录每个子节点创建的先后顺序。基于这个特性，在创建子节点的时候，可以设置这个属性，那么在创建节点过程中，ZK会自动为给定节点名加上一个数字后缀，作为新的节点名。这个数字后缀的范围是整型的最大值。
临时节点（EPHEMERAL）
和持久节点不同的是，临时节点的生命周期和客户端会话绑定。也就是说，如果客户端会话失效，那么这个节点就会自动被清除掉。注意，这里提到的是会话失效，而非连接断开。另外，在临时节点下面不能创建子节点。
临时顺序节点（EPHEMERAL_SEQUENTIAL）
可以用来实现分布式锁
客户端调用create()方法创建名为“_locknode_/guid-lock-”的节点，需要注意的是，这里节点的创建类型需要设置为EPHEMERAL_SEQUENTIAL。
客户端调用getChildren(“_locknode_”)方法来获取所有已经创建的子节点，注意，这里不注册任何Watcher。
客户端获取到所有子节点path之后，如果发现自己在步骤1中创建的节点序号最小，那么就认为这个客户端获得了锁。
如果在步骤3中发现自己并非所有子节点中最小的，说明自己还没有获取到锁。此时客户端需要找到比自己小的那个节点，然后对其调用exist()方法，同时注册事件监听。
之后当这个被关注的节点被移除了，客户端会收到相应的通知。这个时候客户端需要再次调用getChildren(“_locknode_”)方法来获取所有已经创建的子节点，确保自己确实是最小的节点了，然后进入步骤3。


### Zookeeper分布式锁实现(zk怎么实现分布式锁)
https://cloud.tencent.com/developer/article/2060617

首先用到zookeeper中的两个重要知识点：1、zookeeper中的节点类型：临时节点、临时有序节点、持久节点、持久有序节点。临时节点跟session关联。2、zookeeper的watch。以上两点就是实现分布式锁的核心点。

1、创建一个节点lock作为锁的根节点，当有线程需要抢锁的时候在该节点下创建一个临时有序节点

2、节点创建成功后，获取当前根节点下的所有孩子节点列表，并将自己阻塞住

3、因为获取到的子节点列表是无序的，所以需要先对子节点进行排序，然后判断自己是不是当前的第一个子节点，如果自己是第一个子节点说明抢到锁可以执行业务代码

4、如果自己不是第一个子节点，获取到自己当前在列表中索引，去监听自己的前一个节点，也就是自己的索引  index -1   （这里的监听前一个节点为核心，如果我们去监听根节点，那么一个节点的删除就需要回调所有的子节点代价太大，所以是监听前一个节点）

5、当获得锁的节点执行释放锁，也就是删除自己的节点时，后边监听的节点收到回调事件后再去获取所有的子节点，再去判断自己是不是第一个，执行抢锁操作


### zookeeper 常用的3种java客户端
1、zookeeper原生Java API
Zookeeper客户端提供了基本的操作，比如，创建会话、创建节点、读取节点、更新数据、删除节点和检查节点是否存在等。但对于开发人员来说，Zookeeper提供的基本操纵还是有一些不足之处。

Zookeeper API不足之处

（1）Session超时之后没有实现重连机制，需要手动操作；
（2）Watcher注册是一次性的，每次触发之后都需要重新进行注册；
（3）不支持递归创建节点；
（4）异常处理繁琐，Zookeeper提供了很多异常，对于开发人员来说可能根本不知道该如何处理这些异常信息；
（5）只提供了简单的byte[]数组的接口，没有提供针对对象级别的序列化；
（6）创建节点时如果节点存在抛出异常，需要自行检查节点是否存在；
（7）删除节点无法实现级联删除；

基于以上原因，直接使用Zookeeper原生API的人并不多。

        <!-- https://mvnrepository.com/artifact/org.apache.zookeeper/zookeeper -->
        <dependency>
            <groupId>org.apache.zookeeper</groupId>
            <artifactId>zookeeper</artifactId>
            <version>3.7.0</version>
        </dependency>
        
2、ZkClient
<!-- https://mvnrepository.com/artifact/com.101tec/zkclient -->
<dependency>
    <groupId>com.101tec</groupId>
    <artifactId>zkclient</artifactId>
    <version>0.11</version>
</dependency>
ZkClient是一个开源客户端，在Zookeeper原生API接口的基础上进行了包装，更便于开发人员使用。解决如下问题：

1）session会话超时重连
2）解决Watcher反复注册
3）简化API开发

虽然 ZkClient 对原生 API 进行了封装，但也有它自身的不足之处：

几乎没有参考文档；
异常处理简化（抛出RuntimeException）；
重试机制比较难用；
没有提供各种使用场景的实现；


3、Apache Curator
Curator是Netflix公司开源的一套Zookeeper客户端框架，和ZkClient一样，解决了非常底层的细节开发工作，包括连接重连、反复注册Watcher和NodeExistsException异常等。目前已经成为 Apache 的顶级项目。

其特点：

Apache 的开源项目
解决Watch注册一次就会失效的问题
提供一套Fluent风格的 API 更加简单易用
提供更多解决方案并且实现简单，例如：分布式锁
提供常用的ZooKeeper工具类
编程风格更舒服
除此之外，Curator中还提供了Zookeeper各种应用场景（Recipe，如共享锁服务、Master选举机制和分布式计算器等）的抽象封装。

补充说明

Apache Curator是Apache ZooKeeper的Java / JVM客户端库，Apache ZooKeeper是一种分布式协调服务。

它包括一个高级API框架和实用程序，使Apache ZooKeeper更容易和更可靠。它还包括常见用例和扩展（如服务发现和Java 8异步DSL）的配方。

官网：http://curator.apache.org/index.html

Curator项目组件（下载官方源码就可以看到以下组件）

Recipes：    Zookeeper典型应用场景的实现，这些实现是基于Curator Framework。
Framework：  Zookeeper API的高层封装，大大简化Zookeeper客户端编程，添加了例如Zookeeper连接管理、重试机制等。
Utilities：  为Zookeeper提供的各种实用程序。
Client：     Zookeeper client的封装，用于取代原生的Zookeeper客户端（ZooKeeper类），提供一些非常有用的客户端特性。
Errors：  Curator如何处理错误，连接问题，可恢复的例外等。
Maven依赖（地址:https://search.maven.org/search?q=org.apache.curator）

复制代码
GroupID/Org   ArtifactID/Name   描述
org.apache.curator curator-recipes 所有典型应用场景。需要依赖client和framework，需设置自动获取依赖。
org.apache.curator curator-framework 同组件中framework介绍。
org.apache.curator curator-client 同组件中client介绍。
org.apache.curator curator-test 包含TestingServer、TestingCluster和一些测试工具。
org.apache.curator curator-examples 各种使用Curator特性的案例。
org.apache.curator curator-x-discovery 在framework上构建的服务发现实现。
org.apache.curator curator-x-discoveryserver 可以和Curator Discovery一起使用的RESTful服务器。
org.apache.curator curator-x-rpc Curator framework和recipes非java环境的桥接。
复制代码
分布式锁实现

<dependency>
<groupId>org.apache.curator</groupId>
<artifactId>curator-recipes</artifactId>
<version>4.1.0</version>
</dependency>
复制代码
public static void main(String[] args) {
String zookeeperConnectionString = "localhost:2181";
RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
CuratorFramework client = CuratorFrameworkFactory.newClient(zookeeperConnectionString, retryPolicy);
client.start();

try {
//创建分布式锁, 锁空间的根节点路径为/curator/lock
InterProcessMutex lock = new InterProcessMutex(client, "/curator/lock");
if ( lock.acquire(1000, TimeUnit.SECONDS) )
{
try
{
// do some work inside of the critical section here
System.out.println("do some work inside of the critical section here");
}
finally
{
//完成业务流程, 释放锁
lock.release();
}
}

} catch (Exception e) {
e.printStackTrace();
}
}


### 使用 docker 快速启动本地 ZooKeeper 集群
https://xkcoding.com/2021/04/14/run-local-zookeeper-cluster-with-docker.html
有些小伙伴在运行 spring-boot-demo 项目的 demo-zookeeper 模块时，出现了获取不到集群地址的错误

2021-04-14 15:52:18.630 ERROR 23630 --- [ain-EventThread] o.a.c.f.imps.CuratorFrameworkImpl        : Background exception was not retry-able or retry gave up

java.lang.NullPointerException: null
	at org.apache.curator.utils.Compatibility.getHostAddress(Compatibility.java:116) ~[curator-client-5.1.0.jar:na]
	at org.apache.curator.framework.imps.EnsembleTracker.configToConnectionString(EnsembleTracker.java:185) ~[curator-framework-5.1.0.jar:5.1.0]
	at org.apache.curator.framework.imps.EnsembleTracker.processConfigData(EnsembleTracker.java:206) ~[curator-framework-5.1.0.jar:5.1.0]
	at org.apache.curator.framework.imps.EnsembleTracker.access$300(EnsembleTracker.java:50) ~[curator-framework-5.1.0.jar:5.1.0]
	at org.apache.curator.framework.imps.EnsembleTracker$2.processResult(EnsembleTracker.java:150) ~[curator-framework-5.1.0.jar:5.1.0]
	at org.apache.curator.framework.imps.CuratorFrameworkImpl.sendToBackgroundCallback(CuratorFrameworkImpl.java:892) [curator-framework-5.1.0.jar:5.1.0]
	at org.apache.curator.framework.imps.CuratorFrameworkImpl.processBackgroundOperation(CuratorFrameworkImpl.java:649) [curator-framework-5.1.0.jar:5.1.0]
	at org.apache.curator.framework.imps.WatcherRemovalFacade.processBackgroundOperation(WatcherRemovalFacade.java:152) [curator-framework-5.1.0.jar:5.1.0]
	at org.apache.curator.framework.imps.GetConfigBuilderImpl$2.processResult(GetConfigBuilderImpl.java:222) [curator-framework-5.1.0.jar:5.1.0]
	at org.apache.zookeeper.ClientCnxn$EventThread.processEvent(ClientCnxn.java:630) [zookeeper-3.6.0.jar:3.6.0]
	at org.apache.zookeeper.ClientCnxn$EventThread.run(ClientCnxn.java:551) [zookeeper-3.6.0.jar:3.6.0]
	
解决方式如下：

在本机配置 hosts 文件，推荐使用 SwitchHosts! 软件配置。（sudo vim /etc/hosts)
127.0.0.1 zoo1 zoo2 zoo3
修改 Curator 连接 ZooKeeper 集群地址，通过 hostname 方式连接上 ZooKeeper。

- 127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183
+ zoo1:2181,zoo2:2182,zoo3:2183
此时该问题就可以解决了。

另一种解决方式，升级到curator 5.4.0及以上版本


### 使用Curator框架实现分布式锁
https://blog.csdn.net/weixin_47025166/article/details/125451987

### ZooKeeper + Curator 实现的几种分布式锁
https://blog.csdn.net/qq_27818541/article/details/115917267


https://blog.csdn.net/2401_84181536/article/details/138281105
curator提供了四种分布式锁，都实现自接口InterProcessLock；

JAVA-doc：https://curator.apache.org/apidocs/org/apache/curator/framework/recipes/locks/package-summary.html

1> InterProcessMutex

可重入排它锁，每成功加锁一次，就要解锁一次。
2> InterProcessSemaphoreMutex

不可重入排他锁
3> InterProcessReadWriteLock

可重入读写锁，读共享，写互斥；
一个拥有写锁的线程可重入读锁，但是读锁却不能进入写锁。
这意味着写锁可以降级成读锁， 比如请求写锁 —>请求读锁—>释放读锁 —->释放写锁。
4> InterProcessMultiLock

联锁， 将多个锁作为单个实体管理的容器；
当调用acquire()， 所有的锁都会被acquire()，如果请求失败，所有的锁都会被release。 同样调用release时所有的锁都被release(失败被忽略)。



### Curator中的分布式锁解读
https://blog.csdn.net/m0_62436868/article/details/130468165
添加为了Spring配置

### Zookeeper（curator）实现分布式锁案例
https://blog.csdn.net/Saintmm/article/details/128103612
https://zhuanlan.zhihu.com/p/674709638
https://zhuanlan.zhihu.com/p/645540724
https://www.jb51.net/program/298123t36.htm

### SpringBoot整合zookeeper、curator，实现分布式ID生成
https://www.jianshu.com/p/7d58fb6ab8a5
https://gitee.com/kaixinshow/springboot-note

Springboot-Zookeeper(curator)实现分布式锁、分布式ID等
https://blog.csdn.net/W_Meng_H/article/details/127554713

ZooKeeper入门（二）：ZooKeeper常用命令介绍及使用Curator客户端实现分布式配置中心
https://cloud.tencent.com/developer/article/2114567

框架篇：分布式全局唯一ID
https://ost.51cto.com/posts/27703
<dependency>
    <groupId>org.apache.curator</groupId>
    <artifactId>curator-framework</artifactId>
    <version>4.2.0</version>
</dependency>
<dependency>
    <groupId>org.apache.curator</groupId>
    <artifactId>curator-recipes</artifactId>
    <version>4.2.0</version>
</dependency>

RetryPolicy retryPolicy = new ExponentialBackoffRetry(500, 3);
CuratorFramework client = CuratorFrameworkFactory.builder()
      .connectString("localhost:2181")
      .connectionTimeoutMs(5000)
      .sessionTimeoutMs(5000)
      .retryPolicy(retryPolicy)
      .build();
client.start();  
String sequenceName = "root/sequence/distributedId";
DistributedAtomicLong  distAtomicLong = new DistributedAtomicLong(client, sequenceName, retryPolicy);
//使用DistributedAtomicLong生成自增序列
public Long sequence() throws Exception {
    AtomicValue<Long> sequence = this.distAtomicLong.increment();
    if (sequence.succeeded()) {
        return sequence.postValue();
    } else {
        return null;
    }
}

### Zookeeper的基本使用及概述(五) ------ Curator框架的使用
https://blog.csdn.net/weixin_45616375/article/details/127121659


### Zookeeper JAVA API的使用（zookeeper原生Java API使用）
https://www.cnblogs.com/rocky-fang/p/9030438.html


### zookeeper 常用的3种java客户端
https://www.cnblogs.com/felixzh/p/15639247.html
ZooKeeper JavaAPI 操作
https://blog.csdn.net/qq_37435395/article/details/124999051
Zookeeper命令总结
https://blog.csdn.net/hc1285653662/article/details/131936308
深入理解zookeeper——客户端发送请求流程
https://zhuanlan.zhihu.com/p/678046152
ZooKeeper 学习笔记
https://blog.csdn.net/qq_52341269/article/details/130433000
ZK（ZooKeeper）分布式锁实现
https://www.51cto.com/article/687086.html
Zookeeper——Docker下安装部署
https://www.cnblogs.com/caoweixiong/p/12325410.html

### 【仓储物流】美团-即时物流的分布式系统架构设计
https://pdai.tech/md/arch/arch-example-meituan-jishiwuliu.html

### Spring Statemachine应用实践 
https://www.cnblogs.com/88223100/p/Spring-Statemachine-Application-Practice.html
FSM——squirrel状态机使用 （松鼠状态机使用）
https://blog.csdn.net/weixin_45565886/article/details/129197762

### Zookeeper Curator 简单实现；监听Watcher；分布式事务，分布式计数器
https://blog.csdn.net/qq_39246466/article/details/115692202