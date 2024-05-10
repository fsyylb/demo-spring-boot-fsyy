配置redis环境变量 redis config配置

Redis 的配置文件位于 Redis 安装目录下，文件名为 redis.conf！我们可以通过CONFIG命令查看或设置配置项；

①Redis CONFIG 命令格式如下：

redis 127.0.0.1:6379> CONFIG GET CONFIG_SETTING_NAME         

同时我们可以用 get * 获取Redis的所有配置项，我们可以尝试如下命令行：
127.0.0.1:6379> config get daemonize         
          127.0.0.1:6379> config get *         

②CONFIG参数说明

redis.conf 配置项说明如下：

1. Redis默认不是以守护进程的方式运行，可以通过该配置项修改，使用yes启用守护进程        daemonize no

2. 当Redis以守护进程方式运行时，Redis默认会把pid写入/var/run/redis.pid文件，可以通过pidfile指定        pidfile /var/run/redis.pid

3. 指定Redis监听端口，默认端口为6379，作者在自己的一篇博文中解释了为什么选用6379作为默认端口，因为6379在手机按键上MERZ对应的号码，而MERZ取自意大利歌女Alessia Merz的名字        port 6379

4. 绑定的主机地址        bind 127.0.0.1

5.当 客户端闲置多长时间后关闭连接，如果指定为0，表示关闭该功能        timeout 300

6. 指定日志记录级别，Redis总共支持四个级别：debug、verbose、notice、warning，默认为verbose        loglevel verbose

7. 日志记录方式，默认为标准输出，如果配置Redis为守护进程方式运行，而这里又配置为日志记录方式为标准输出，则日志将会发送给/dev/null        logfile stdout

8. 设置数据库的数量，默认数据库为0，可以使用SELECT <dbid>命令在连接上指定数据库id        databases 16

9. 指定在多长时间内，有多少次更新操作，就将数据同步到数据文件，可以多个条件配合        save <seconds> <changes>        Redis默认配置文件中提供了三个条件：        save 900 1        save 300 10        save 60 10000        分别表示900秒（15分钟）内有1个更改，300秒（5分钟）内有10个更改以及60秒内有10000个更改。

10. 指定存储至本地数据库时是否压缩数据，默认为yes，Redis采用LZF压缩，如果为了节省CPU时间，可以关闭该选项，但会导致数据库文件变的巨大        rdbcompression yes

11. 指定本地数据库文件名，默认值为dump.rdb        dbfilename dump.rdb

12. 指定本地数据库存放目录        dir ./

13. 设置当本机为slav服务时，设置master服务的IP地址及端口，在Redis启动时，它会自动从master进行数据同步        slaveof <masterip> <masterport>

14. 当master服务设置了密码保护时，slav服务连接master的密码        masterauth <master-password>

15. 设置Redis连接密码，如果配置了连接密码，客户端在连接Redis时需要通过AUTH <password>命令提供密码，默认关闭        requirepass foobared

16. 0，表示不作限制。当客户端连接数到达限制时，Redis会关闭新的连接并向客户端返回max number of clients reached错误信息        maxclients 128

17.指定Redis最大内存限制，Redis在启动时会把数据加载到内存中，达到最大内存后，Redis会先尝试清除已到期或即将到期的Key，当此方法处理后，仍然到达最大内存设置，将无法再进行写入操作，但仍然可以进行读取操作。Redis新的vm机制，会把Key存放内存，Value会存放在swap区        maxmemory <bytes>

18.指定是否在每次更新操作后进行日志记录，Redis在默认情况下是异步的把数据写入磁盘，如果不开启，可能会在断电时导致一段时间内的数据丢失。因为redis本身同步数据文件是按上面save条件来同步的，所以有的数据会在一段时间内只存在于内存中。默认为no        appendonly no

19. 指定更新日志文件名，默认为appendonly.aof         appendfilename appendonly.aof

20. 指定更新日志条件，共有3个可选值：        no：表示等操作系统进行数据缓存同步到磁盘（快）        always：表示每次更新操作后手动调用fsync()将数据写到磁盘（慢，安全）        everysec：表示每秒同步一次（折衷，默认值）        appendfsync everysec

21.指定是否启用虚拟内存机制，默认值为no，简单的介绍一下，VM机制将数据分页存放，由Redis将访问量较少的页即冷数据swap到磁盘上，访问多的页面由磁盘自动换出到内存中（在后面的文章我会仔细分析Redis的VM机制）         vm-enabled no

22. 虚拟内存文件路径，默认值为/tmp/redis.swap，不可多个Redis实例共享         vm-swap-file /tmp/redis.swap

23.将所有大于vm-max-memory的数据存入虚拟内存,无论vm-max-memory设置多小,所有索引数据都是内存存储的(Redis的索引数据就是keys),也就是说,当vm-max-memory设置为0的时候,其实是所有value都存在于磁盘。默认值为0         vm-max-memory 0

24. Redis swap文件分成了很多的page，一个对象可以保存在多个page上面，但一个page上不能被多个对象共享，vm-page-size是要根据存储的数据大小来设定的，作者建议如果存储很多小对象，page大小最好设置为32或者64bytes；如果存储很大大对象，则可以使用更大的page，如果不 确定，就使用默认值         vm-page-size 32

25. 设置swap文件中的page数量，由于页表（一种表示页面空闲或使用的bitmap）是在放在内存中的，，在磁盘上每8个pages将消耗1byte的内存。         vm-pages 134217728

26. 设置访问swap文件的线程数,最好不要超过机器的核数,如果设置为0,那么所有对swap文件的操作都是串行的，可能会造成比较长时间的延迟。默认值为4         vm-max-threads 4

27. 设置在向客户端应答时，是否把较小的包合并为一个包发送，默认为开启        glueoutputbuf yes

28. 指定在超过一定的数量或者最大的元素超过某一临界值时，采用一种特殊的哈希算法        hash-max-zipmap-entries 64        hash-max-zipmap-value 512

29. 指定是否激活重置哈希，默认为开启（后面在介绍Redis的哈希算法时具体介绍）        activerehashing yes

30. 指定包含其它的配置文件，可以在同一主机上多个Redis实例之间使用同一份配置文件，而同时各个实例又拥有自己的特定配置文件        include /path/to/local.conf

③Config的编辑和配置我们可以通过修改 redis.conf 文件或使用 CONFIG set 命令来修改配置！CONFIG SET 命令基本语法如下：
redis 127.0.0.1:6379> CONFIG SET CONFIG_SETTING_NAME NEW_CONFIG_VALUE         

我们可以执行如下命令尝试：

redis 127.0.0.1:6379> CONFIG SET loglevel verbose         
          redis 127.0.0.1:6379> CONFIG GET loglevel         
我们可以看到最终结果如下



### 
暴露端口，但不映射到宿主机，只被连接的服务访问。

expose:
 - "3000"
 - "8000"

详解
在 Docker Compose 中，expose 关键字用于指定容器暴露的端口，但它并不会在主机和容器之间创建端口映射（port mapping）。它只是在容器内部定义了一个端口，以便其他容器可以访问该端口。

使用 expose 关键字可以在 Compose 文件中声明容器暴露的端口。这对于容器内部的服务之间进行通信非常有用。

以下是 expose 关键字的示例用法：

services:
  myservice:
    image: myimage
    expose:
      - 8080
https://blog.csdn.net/a772304419/article/details/132913562


### Redis Cluster 简介
https://zhuanlan.zhihu.com/p/196781773

Redis Cluster 作为Redis集群的官方实现, 自然是搭建Redis集群的重要选择之一. 本文主要翻译自Redis cluster tutorial 官方教程, 外加一些个人在学习中的理解和笔记, 可当作一篇简单的随笔, 可能会有不少的翻译或理解错误. 本文会随着原文或实践持续更新或更正.

Redis Cluster 101
Redis Cluster 提供了一种自动将数据分布到多个Redis节点的方法实现, 同时提供某种程度的可用性保证, 即即使部分节点失效或失去联系, 集群仍能提供服务. 然而, 如果出现大规模失效(例如大部分主节点无法提供服务), 集群将停止运作.

实际上, Redis Cluster 可提供:

自动分发数据到不同节点的能力
部分节点失效或失联后继续提供服务的能力
Redis Cluster TCP 端口
每个集群节点需要打开两个TCP连接——普通Redis端口监听客户端连接, 外加一个在监听客户端端口上增加10000的数据端口. 比如监听6379端口为客户端服务, 那么则另外再监听16379端口用于集群桥接(Cluster bus). 所谓Cluster bus, 即使用一种二进制协议(binary protocol)进行集群内点对点(node-to-node)通讯, 包括节点失效检测, 配置更新, 故障转移(failover)认证等等. 客户端永远只应连接普通的(较低的)端口, 不应尝试连接集群桥接(较高的)端口.

命令端口(command port)与集群桥接端口(cluster bus port)恒定相差10000.

集群节点间通讯使用一种独特的二进制协议, 更适合于节省消息交互所需的带宽和时间.

Redis Cluster 与 Docker
Redis Cluster 不支持NATted环境和IP地址或TCP端口被重映射(remapped)的环境.

Docker使用一种名叫port mapping的技术, 运行于Docker容器内的程序实际对外使用的端口和监听的端口可不相同. 为了让Docker 兼容Redis Cluster, 需要使用Docker的host network模式. 参阅Docker Reference中的 --net=host 参数.

* 这里提到Redis Cluster 不支持NATted环境, 但根据6.0源码中的redis.conf 注释介绍, 目前若节点运行于类似普通Docker之类的容器环境, 已经可以通过

cluster-announce-ip
cluster-announce-port
cluster-announce-bus-port
三个配置参数静态设定节点的运行参数, 包括对外IP, 普通Redis命令端口和集群桥接端口. 这些信息会放在与其它节点通讯的数据包的头部(the header of the bus packets), 以令其它节点获得当前节点的信息. 通过设置参数, 命令端口与集群桥接端口不需要恒定相差10000, 所以两个端口数值可任意设置. 如果没有设置, 默认将使用Redis Cluster 的自动检测获得本机IP地址, 并使用两个相差10000的端口. 从github项目的代码仓库可以得知, 从4.0开始支持以上三个参数.

Redis Cluster 数据分片
Redis Cluster 不使用一致性散列(consistent hashing), 而是使用一种不同的分片方式——每个key概念上都是散列槽(hash slot)的一员.

Redis Cluster 共有16384个散列槽. 计算key属于哪个散列槽的方法, 则是简单地对key作CRC16后将结果对16384取余.

每个节点各负责一部分的散列槽. 比如说, 一个集群有三个节点, 其中

Node A 负责0 至 5500散列槽
Node B 负责5501 至 11000散列槽
Node C 负责11001 至 16383散列槽
这样, 可以方便地从集群中新增或移除节点. 比如需要新增一个节点D, 只需要从A, B, C三个节点各分部分散列槽到D. 同样地, 要移除A, 只需要将A负责的散列槽分发给B和C, 当A不再负责任何散列槽, 则可以安全地从集群中移除.

节点间移动散列槽不需要停止Redis服务, 所以增删节点或重新分配散列槽不会造成服务暂停.

Redis Cluster 允许支持对多个key的操作(multiple key operations), 即, 对一个命令(或整个事务、一个Lua脚本)中, 所有涉及到的key, 必须属于同一个散列槽. 如果需要保证不同的key都可算得相同的散列槽, 可以使用散列标签(hash tags)的概念. 简单来说, 如果一个key里面包含一对大括号{}, 且括号中间含有字符串, 则只会对括号中的字符串作散列计算. 例如对 "this{foo}key" 和 "another{foo}key" 两个key, 只会对括号中的foo计算hash值得出对应的散列槽, 所以可以保证两个key均属于同一散列槽, 可在集群中用作多key命令中的参数.

Redis Cluster 主从模型

为了保证即使部分主节点失效或无法与其它节点通讯, 集群仍能维持服务, Redis Cluster 使用一种主从模型, 每个散列槽分配给1个(主节点自己)到N个(N-1个额外的从节点)节点. 比如有A, B, C三个主节点和A1, B1, C1三个从节点, A1, B1, C1分别为A, B, C的复制(replica)(注: 即A1, B1, C1最终数据与A, B, C保持一致). 如果B失效, 集群会推举B1作为新的主节点, 维持集群的正常运作. 然而如果B和B1同时失效, 集群将无法继续提供服务.

Redis Cluster 一致性保证

Redis Cluster 无法提供强一致性保证. 实际上, 特定情形下, Redis Cluster 可能会丢失已与客户端确认的写入(注: 即客户端向Redis发送写入命令时, 已收到Redis返回的OK, 但集群仍然丢失了这次客户端写入的数据). 其中首要原因, 是因为主从节点间使用的是异步复制. 这意味着当写入发生时:

客户端向主节点B发送写入命令
主节点B完成写入并回复客户端 OK
主节点B向其从节点B1, B2, B3(如果有)传递这次的写入操作
如上, B不会等B1, B2, B3确认成功写入后再回复客户端, 因为这对Redis来说延迟太严重了. 所以如果客户端写入数据, B确认了写入成功, 但还未来得及将这次写入操作发给从节点就崩溃了, 其中一个从节点(显然还没收到这次客户端的写入)被推举为主节点, 将永远丢失了这次的写入操作.

如果确实有需要, Redis Cluster 可通过WAIT 命令实现同步写入, 这将另写入丢失大大地减少. 然而值得注意的是, 这并不意味着Redis Cluster可实现强一致性. 永远存在着一种可能, 一个从节点, 因为种种原因无法接收到最后的写入操作, 却在主节点失效后被推举为新的主节点.

另外还有一种导致写入丢失的场景值得注意——当出现网络分区(network partition)(注: 即每个分区中的节点短暂性地不能与其它分区节点通讯), 客户端和至少一个主节点被隔离到拥有较少节点的分区.

仍然假设有六个节点A, B, C, A1, B1, C1, 三主三从, 外加一个客户端, 称为Z1. 当出现网络分区时, 假设一种场景, 一边分区拥有A, C, A1, B1, C1, 另一边拥有B和Z1. Z1仍然可以向B写入数据. 如果分区可于短时间内恢复, 集群即可继续正常地运行. 然而, 如果网络分区持续时间足够长, 在包含多数节点(majority side)的分区中B1被推举为主节点, 那么Z1于网络分区后向B写入的数据将被丢失.

分区发生后(即相当于B与Z1被隔离开来), Z1向B写入数据有一个最大窗口时间, 即, 如果分区持续足够长的时间, 导致多数节点一方重新推举出新的主节点代替位于少数节点一方的主节点, 所有位于少数节点一方的原主节点将停止接受任何写入命令. 这个持续时间的长短设置对Redis Cluster 来说相当重要, 被称之为node timeout. 换言之, 经过node timeout时间后, 一个无法通讯的主节点将被认为已失效, 可以被其中一个从节点取代(严格来说, 这里应该是如果某个节点无法与另外一个节点通讯超过node timeout时间, 会认为那个节点可能已失效, 并将这个猜想发给其它可以通讯的节点并收集其它节点的反馈, 详见下文), 同样地, 一个主节点无法感知其它大部分主节点超过node timeout时间, 将进入错误状态并停止接受任何写入命令.

Redis Cluster 配置参数

Redis Cluster 通过节点使用redis.conf 文件配置集群参数. 其中有些参数顾名思义, 有些则需要稍加解释.

cluster-enabled <yes/no>: 是否开启集群功能. no表示该Redis实例将独立运行.
cluster-config-file<filename>: 注意这个选项定义的文件不是一个用户可编辑的配置文件, 而是由Redis Cluster 自动生成和修改, 用来保存集群配置(主要是每次改变后的状态)的文件, 用来节点重启时重新读取之前的配置和状态. 文件记录了集群中的其它节点及其状态, 需要保存的变量等等. 节点接收到某些讯息会重写这个文件.
cluster-node-timeout<milliseconds>: 节点从不可用(无法通讯)到被标记为失效允许的最大持续时间, 这个时间称为node timeout. 如果一个主节点无法通讯超过这个时间, 将被其从节点执行故障转移(fail over). 这个参数还有一个重要的用处, 每个节点若无法与大部分节点通讯超过这个时间, 将停止接受新的客户端请求.
cluster-slave-validity-factor<factor>: 如果一个从节点设为0, 无论其与对应的主节点断开连接多长时间, 仍然可以取代主节点成为新的主节点. 如果设为正数, 通过node timeout * factor(该参数设置的值) 得出最大允许断开连接时间(maximum disconnection time). 如果这是一个从节点, 与其对应的主节点断开连接超过这个时间的话, 将不会尝试取代而成为新的主节点. 举个例子, 如果node timeout 设为5秒, factor 设为10, 那这个从节点与其主节点断开连接超过50秒后将不会尝试取代原主节点. 注意这个参数设为任意非0值可能会出现因某主节点失效但没有从节点允许取而代之而导致集群无法运作. 这种情况下集群仅当原主节点恢复正常并重新加入到集群中才能重新提供服务.
cluster-migration-barrier<count>: 当需要从节点迁移(replica migration)时(通常出现在从节点触发故障转移成为新的主节点后, 没有从节点关联到新主节点, 做新主节点的备份), 保证迁移的节点原来关联的主节点仍然持有从节点的最小数量. (换言之, 如果该值设为2, 需要从节点迁移时, 只会从关联从节点数量大于等于3的主节点获取迁移的从节点, 保证迁移后原主节点至少关联2个从节点).
cluster-require-full-coverage<yes/no>: 默认为yes, 表示如果有任何散列槽没有分配给任意一个主节点, 集群将停止接受写入命令. 反之设为no, 集群可正常接受写入命令, 但仅有部分命令(只包含对应散列槽已被分配的key的命令)可被执行.
cluster-allow-reads-when-down<yes/no>: 默认为no, 表示当集群因主节点数量达不到最小值或有散列槽没有分配而被标记为失效时, 节点将停止所有的客户端通讯(stop serving all traffic). 这样可以避免潜在从一个不知道集群状态变化的节点读到不一致数据的危险. 设为yes则允许集群失效时仍可以由节点中读取数据. 这样既保证读操作的高可用性, 亦避免不一致写操作(inconsistent writes). 同时, 当Redis Cluster 仅包含1至2个节点, 而某个节点失效后无可用从节点替代, 且因节点数量不足, 无法自动重新分配散列槽, 则该参数设为yes可保证节点仍然可执行读操作.
* 对于cluster-allow-reads-when-down 配置, 原文提到第二种场景(节点数量不足)中设为yes 可提供写入操作(as it allows the nodes to continue serving writes when a master fails but automatic failover is impossible). 但根据v6.0源码中redis.conf 关于该参数的注释和该参数含义的理解, 应只保证提供读取操作.

Redis Cluster 的创建和使用

创建一个Redis集群前, 首要任务是创建一些以集群模式(cluster mode)运行的Redis实例. 这意味着集群不是由普通Redis实例组成的, 而是由配置成集群模式, 开启了集群功能, 可执行集群指令的实例组成的. 简单的集群实例配置文件如下:

port 7000
cluster-enabled yes
cluster-config-file nodes.conf
cluster-node-timeout 5000
appendonly yes
一个功能正常的集群至少需要三个主节点.

节点启动时, 会读取配置文件中指定的cluster-config-file(默认为nodes.conf), 里面包含了一个唯一ID. 如果文件不存在, 表示这是一个新的节点, 会自动生成一个新的唯一ID, 并保存于cluster-config-file内, 用来永远标识这个节点. 节点间辨认通过这个ID, 而不是IP或端口. IP或端口可以修改, 但唯一标识符于整个节点生命周期内都不会更改. 这个ID称之为Node ID.

Redis 5 的客户端程序可直接进行集群操作, 包括创建集群, 检查集群状态, 重新分片等等. 而Redis 3 或 4 则需要借助工具redis-trib.rb, 使用与Redis 5 的redis-cli 相似, 但必须先安装redis gem(install redis gem)才能运行redis-trib. 注意Redis 5 的客户端可连接Redis 4 的集群使用. 大部分的集群命令都可连接任意一个集群节点后使用, 正常状态下对数据的操作任意节点均会根据涉及到的key计算对应散列槽后直接执行或返回节点重定向(告诉客户端应该将命令发送到哪个节点), 而对集群配置的修改最终将通过节点间的通讯扩散到所有节点.

手动执行故障转移(Manual failover)

有时即使主节点没有任何故障, 仍然需要其某个从节点强制取代为新主节点. 手动执行故障转移需要对希望取代为主节点的从节点发送CLUSTER FAILOVER 命令. 相对因实际的主节点失效而自动执行的故障转移来说, 手动执行更加安全. 因为仅当系统确定新主节点已处理完所有旧主节点发过来的复制流(replication stream)(即新主节点已完全同步旧主节点的所有数据, 两个节点数据完全一致), 才会重定向客户端到新的主节点, 从而避免故障转移过程中的数据丢失.

当开始手动执行故障转移实际开始前, 原主节点所有客户端连接都被停止, 同时原主节点将其复制偏移(replication offset)发送给将要取代自己的从节点(可理解为将数据同步到从节点), 然后等待从节点完成同步, 开始故障转移, 最后原主节点收到新主节点配置更新的通知, 恢复所有客户端连接, 然后将所有客户端重定向到新主节点.

添加新节点

添加一个新节点基本分为两步: 向集群新增一个空节点, 然后从其它主节点分配部分散列槽和数据到新节点作为主节点, 或者配置为某个原有主节点的从节点.

移除节点

可以任意移除一个从节点, 但对于主节点, 仅可移除一个空的主节点, 或者先利用手动执行故障转移将主节点变为从节点再移除.



by Reginald Lee

2020.09.06



ref.

Redis cluster tutorial

relations.

Redis Cluster 深入探究
​

cluster-announce-ip
cluster-announce-port
cluster-announce-bus-port
还是没搞懂怎么使用的？能详细解释一下吗？正常容器启动的时候映射宿主机端口号，然后就可以根据映射端口号访问了，但是集群模式是通过命令获得具体Redis的ip信息，由于容器内，所以宿主机返回的IP和端口信息返回的是容器内部的IP端口信息，并不是宿主机的IP信息。
我测试的是如果配置上面配置项为宿主机IP端口信息，集群创建的时候，node之间又无法连接了


### 在docker中部署redis cluster，解决Waiting for the cluster to join...问题和支持spring的访问
https://blog.csdn.net/qq_40693970/article/details/131926087?utm_medium=distribute.pc_relevant.none-task-blog-2~default~baidujs_baidulandingword~default-1-131926087-blog-104603899.235^v43^pc_blog_bottom_relevance_base7&spm=1001.2101.3001.4242.2&utm_relevant_index=4
背景
在学习redis高级篇的时候，卡在了redis集群的部署。
因为教程不是基于docker进行部署的，所以自己在部署的时候遇到了一些问题，看到一些博客说了他们的做法但是我还是搭建不起来，然后看了redis.io的文档，也没有发现有docker部署的说明
所以自己静心梳理了一番。
跟着学的教程没有用docker部署，然后配置的时候没有配置

cluster-announce-** xxxxx

但是配置的是哨兵模式的

replica-announce-** xxxxx

所以搭建内部调试的时候没问题，但是spring连接不上
一番修改之后，出现搭建都搭不起来的现象

以下仅作本人学习过程的记录

遇到的问题
在使用命令创建集群的时候

redis-cli -a 密码 --cluster create --cluster-replicas 从节点数 ip1:port1 ip2:port2 

控制台出现以下阻塞信息

Waiting for the cluster to join
..................

经查阅，可能是各节点无法通信造成的，需要设置声明的对外的端口号+10000

问题解决
一共有6个节点
每个节点的端口都设置成6379
因为docker的隔离性
所以每个节点的配置文件都是下面这一份，直接复制

redis.conf配置文件
port 6379
bind 0.0.0.0
# 一定要手动打开集群模式
cluster-enabled yes
cluster-node-timeout 5000
dir ./
# 我设置需要密码访问
protected-mode yes
requirepass 123456

docker-compose.yml
说一下重点
1.我的网络模式是bridge模式
我使用过host模式
但是最后java访问节点失败
所以选择bridge模式

2.以下几行配置的关键性

 ports:
   # 容器内的端口与配置文件保持一致
    # 容器外的端口与下面声明的对外端口保持一致
   - 6379:6379
   # 容器内集群通信端口默认是配置的端口+10000
   # 容器外的集群通信端口与下面声明的对外集群通信端口保持一致
   - 16379:16379
 command: 
   - "redis-server" 
   - "/etc/redis/redis.conf"
   # 密码一定要配置且与配置文件的requirepass一致
   - "--masterauth 123456"
   # 集群对外声明的ip，用于集群的搭建和spring的访问
   # 这里需要设置为宿主机的ip，而且一定要配置
   - "--cluster-announce-ip 192.168.1.7"
   # 集群对外声明的端口，用于集群的搭建和spring的访问
   - "--cluster-announce-port 6379"
   # 集群对外声明的总线端口，用于集群之间通信
   # 一定要设置，否则创建集群的时候出现阻塞
   - "--cluster-announce-bus-port 16379"

集群搭建命令
进入某个docker容器中使用以下命令搭建
三主三从集群

redis-cli -a 123456 --cluster create --cluster-replicas 1 192.168.1.7:6379 192.168.1.7:6380 192.168.1.7:6381 192.168.1.7:6382 192.168.1.7:6383 192.168.1.7:6384

完整版docker-compose.yml
version: "3"
services:
 redis1:
  image: redis:7.0
  container_name: redis1
  ports:
   - 6379:6379 # 注意这里
   - 16379:16379 # 注意这里
  volumes:
   - ./redis1/redis.conf:/etc/redis/redis.conf
   - ./redis1/data:/data
  command: 
   - "redis-server" 
   - "/etc/redis/redis.conf"
   - "--masterauth 123456"
   - "--cluster-announce-ip 192.168.1.7"
   - "--cluster-announce-port 6379" # 注意这里
   - "--cluster-announce-bus-port 16379" # 注意这里
  networks:
    - redis-net

 redis2:
  image: redis:7.0
  container_name: redis2
  ports:
   - 6380:6379 # 注意这里
   - 16380:16379 # 注意这里
  volumes:
   - ./redis2/redis.conf:/etc/redis/redis.conf
   - ./redis2/data:/data
  command: 
   - "redis-server" 
   - "/etc/redis/redis.conf"
   - "--masterauth 123456"
   - "--cluster-announce-ip 192.168.1.7"
   - "--cluster-announce-port 6380" # 注意这里
   - "--cluster-announce-bus-port 16380" # 注意这里
  networks:
    - redis-net

 redis3:
  image: redis:7.0
  container_name: redis3
  ports:
   - 6381:6379 # 注意这里
   - 16381:16379 # 注意这里
  volumes:
   - ./redis3/redis.conf:/etc/redis/redis.conf
   - ./redis3/data:/data
  command: 
   - "redis-server" 
   - "/etc/redis/redis.conf"
   - "--masterauth 123456"
   - "--cluster-announce-ip 192.168.1.7"
   - "--cluster-announce-port 6381" # 注意这里
   - "--cluster-announce-bus-port 16381" # 注意这里
  networks:
    - redis-net

 slave1:
  image: redis:7.0
  container_name: slave1
  ports:
   - 6382:6379 # 注意这里
   - 16382:16379 # 注意这里
  volumes:
   - ./slave1/redis.conf:/etc/redis/redis.conf
   - ./slave1/data:/data
  command: 
   - "redis-server" 
   - "/etc/redis/redis.conf"
   - "--masterauth 123456"
   - "--cluster-announce-ip 192.168.1.7"
   - "--cluster-announce-port 6382" # 注意这里
   - "--cluster-announce-bus-port 16382" # 注意这里
  networks:
    - redis-net

 slave2:
  image: redis:7.0
  container_name: slave2
  ports:
   - 6383:6379 # 注意这里
   - 16383:16379 # 注意这里
  volumes:
   - ./slave2/redis.conf:/etc/redis/redis.conf
   - ./slave2/data:/data
  command: 
   - "redis-server" 
   - "/etc/redis/redis.conf"
   - "--masterauth 123456"
   - "--cluster-announce-ip 192.168.1.7"
   - "--cluster-announce-port 6383" # 注意这里
   - "--cluster-announce-bus-port 16383" # 注意这里
  networks:
    - redis-net

 slave3:
  image: redis:7.0
  container_name: slave3
  ports:
   - 6384:6379
   - 16384:16379
  volumes:
   - ./slave3/redis.conf:/etc/redis/redis.conf
   - ./slave3/data:/data
  command: 
   - "redis-server" 
   - "/etc/redis/redis.conf"
   - "--masterauth 123456"
   - "--cluster-announce-ip 192.168.1.7"
   - "--cluster-announce-port 6384" # 注意这里
   - "--cluster-announce-bus-port 16384" # 注意这里
  networks:
    - redis-net

networks:
  redis-net:
    driver: bridge
    
    
### docker搭建redis cluster 一直等待节点加入集群 （ Waiting for the cluster to join ）
https://blog.csdn.net/qq_36573407/article/details/104603899

### 基于Docker搭建Redis集群，以及Waiting for the cluster to join等问题分析、解决
https://blog.csdn.net/weixin_46053046/article/details/120102997
基于Docker搭建Redis集群，以及槽位问题分配等问题解决
前言
一、Redis集群搭建
1.拉取镜像
2.创建6个Redis节点
3.初始化Redis节点
二、搭建问题问题
1.Waiting for the cluster to join
2.开启认证
3.开启AOF
三、获取镜像详细信息
总结
前言
该篇博文，主要介绍如何基于Docker搭建Redis集群，以及解决Redis集群搭建过程中，"Waiting for the cluster to join"环节阻塞等问题。

一、Redis集群搭建
1.拉取镜像
通过docker search redis，查询当前提供的Redis镜像，然后选择一个合适的镜像，基于docker pull命令下载即可。


当然，也可以通过诸如wget下载Redis文件、编译、执行redis-server等命令，编写Docker File文件，然后生成一个Docker镜像，这里不再阐述。

2.创建6个Redis节点
Redis镜像获取成功后，通过docker run命令创建6个镜像，这里先提供一种创建方式，在后续解答"Waiting for the cluster to join"环节，再提供另外一种。

docker run -d --net host --name redis-node1 -v /home/install/redis/data/node1:/data redis --cluster-enabled yes --cluster-config-file nodes-node-1.conf --port 6379
docker run -d --net host --name redis-node2 -v /home/install/redis/data/node2:/data redis --cluster-enabled yes --cluster-config-file nodes-node-2.conf --port 6380
docker run -d --net host --name redis-node3 -v /home/install/redis/data/node3:/data redis --cluster-enabled yes --cluster-config-file nodes-node-3.conf --port 6381
docker run -d --net host --name redis-node4 -v /home/install/redis/data/node4:/data redis --cluster-enabled yes --cluster-config-file nodes-node-4.conf --port 6382
docker run -d --net host --name redis-node5 -v /home/install/redis/data/node5:/data redis --cluster-enabled yes --cluster-config-file nodes-node-5.conf --port 6383
docker run -d --net host --name redis-node6 -v /home/install/redis/data/node6:/data redis --cluster-enabled yes --cluster-config-file nodes-node-6.conf --port 6384


通过docker ps，可查看到之前创建的6个实例。到此，6个Redis节点创建完成。

3.初始化Redis节点
进入到redis-node1容器，通过cluster info命令，查看集群状态

由于此时，尚未进行集群槽位分配等操作，因此，当前集群状态为fail，且cluster_slots_ok数为0。接下来，在redis-node1容器中，执行redis-cli --cluster create命令，进行集群初始化

redis-cli --cluster create 192.168.0.100:6379  192.168.0.100:6380 192.168.0.100:6381 192.168.0.100:6382 192.168.0.100:6383 192.168.0.100:6384 --cluster-replicas 1

上述命令执行后，在此执行cluster info命令，此时cluster_state为ok，且cluster_slot_ok为16384。

简单操作一下Redis，命令成功执行。

同时，Redis Desktop Manager工具也能访问刚搭建的Redis集群，到此，Redis集群成功创建。


二、搭建问题问题
1.Waiting for the cluster to join
笔者在刚开始使用Docker搭建Redis集群的时候，在redis-cli --cluster create环节，一直卡到"Waiting for the cluster to join”环节。百思不得其解，后续通过Redis官网，耗费一段时间后，才得以解决。

Redis集群中的各个节点，需要开放一个端口，同其他节点建立连接，用于接收心跳数据等操作。也就是说，redis-node1节点，开放6379端口供client连接时，同时提供16379端口(10000 + 6379)，供其他Redis节点连接。

集群初始化过程中，需要同其他Redis建立连接，进行通信。若节点间无法连接，此时会阻塞，这也就是一直阻塞到"Waiting for the cluster to join"环节的原因。

细心的读者，不难发现，上述给出Redis节点创建命令，通过–net host，指定网络类型为host，使得容器与宿主机使用同一网络，从而规避了这类问题。

当然，若不想修改容器网络类型的话，则，需要同时暴露两个端口，用于提供client和其他节点，进行通信。命令如下所示

docker run -d --name redis-node1 -v /home/install/redis/data/node1:/data -p 6379:6379 -p 16379:16379 redis --cluster-enabled yes --cluster-config-file nodes-node-1.conf --port 6379 
docker run -d --name redis-node2 -v /home/install/redis/data/node2:/data -p 6380:6380 -p 16380:16380 redis --cluster-enabled yes --cluster-config-file nodes-node-2.conf --port 6380 
docker run -d --name redis-node3 -v /home/install/redis/data/node3:/data -p 6381:6381 -p 16381:16381 redis --cluster-enabled yes --cluster-config-file nodes-node-3.conf --port 6381 
docker run -d --name redis-node4 -v /home/install/redis/data/node4:/data -p 6382:6382 -p 16382:16382 redis --cluster-enabled yes --cluster-config-file nodes-node-4.conf --port 6382 
docker run -d --name redis-node5 -v /home/install/redis/data/node5:/data -p 6383:6383 -p 16383:16383 redis --cluster-enabled yes --cluster-config-file nodes-node-5.conf --port 6383 
docker run -d --name redis-node6 -v /home/install/redis/data/node6:/data -p 6384:6384 -p 16384:16384 redis --cluster-enabled yes --cluster-config-file nodes-node-6.conf --port 6384 

2.开启认证
通过上述介绍方式，创建的Redis集群，是没有认证功能的。那么，如何创建需要认证的集群呢？其实也简单，在创建Redis节点的时候，指定–requirepass XXX参数，开启认证(XXX，指的是认证密码)。有一个特别需要注意的是，若创建的节点需要认证，那么在集群初始化时，需要额外通过-a参数，指定节点认证密码，否则，会出现如下错误：
[ERR] Node 192.168.0.100:6379 NOAUTH Authentication required

docker run -d --name redis-node1 -v /home/install/redis/data/node1:/data -p 6379:6379 -p 16379:16379 redis --cluster-enabled yes --cluster-config-file nodes-node-1.conf --port 6379 --requirepass xxx
……//省略其余5个节点

redis-cli --cluster create 192.168.0.100:6379  192.168.0.100:6380 192.168.0.100:6381 192.168.0.100:6382 192.168.0.100:6383 192.168.0.100:6384 -a xxx --cluster-replicas 1

3.开启AOF
进入到Redis，通过config get appendonly命令，发现，上述创建的集群，没有开启AOF持久化。

那么，如果想开启AOF持久化，要怎么操作呢？笔者这边，想到如下两种方式，开启AOF持久化

第一种，和"开启认证"一样，在创建容器的时候，添加–appendonly yes参数。
第二种，进入Redis节点容器，通过redis-cli，添加参数。
redis-cli -c -h 192.168.0.100 -p 6379 -c config set appendonly yes
…… //省略其余5个

// 开启认证的时候，添加参数(请自行替换密码)
redis-cli -a que.longjiang --cluster call config set appendonly yes

进入到/data目录，可以发现，此时同时存在了rdb和aof文件，代表aof持久化成功。


三、获取镜像详细信息
上述提供的容器创建命令，指定了一些参数，如开启集群、端口等。那么，想必各位读者比较好奇，为什么是这些参数，以及还可以设置哪些其他参数呢？

要想解答这个问题，就需要知道这个镜像的创建细节。可通过docker inspect、docker history等命令获取镜像创建信息。

如下为docker inspect redis命令输出结果，通过该结果，可以知道该镜像暴露的端口、指定命令、工作目录等信息。

另一个比较直观的是docker history xxx命令，通过该命令，可以很明显查询该镜像各层信息。其中CREATED BY部分是截断显示，可添加–no-trunc命令，查看完整信息。

通过该命令，可以大致知道该镜像Docker File文件内容。

不难发现，当前镜像从Redis官网下载Redis后，进行编译等一系列操作后，生成的镜像。也就是说，Redis官方支持的参数，这里都可以指定。

总结
通过docker search redis 以及docker pull redis，获取Redis镜像。
通过docker run创建6个Redis节点，通过–net host或-p 暴露端口，使得各节点能够相互通信。
通过edis-cli --cluster create进行槽位分配。
通过redis-cli --cluster，对集群所有节点，进行相关操作，如，设置参数等。
通过docker inspect redis、docker history redis查看redis镜像细节。


------------
docker run -d --name redis-node1  -p 6379:6379 -p 16379:16379 redis:6.0.9 --cluster-enabled yes --cluster-config-file nodes.conf --port 6379 
docker run -d --name redis-node2  -p 6380:6380 -p 16380:16380 redis:6.0.9 --cluster-enabled yes --cluster-config-file nodes.conf --port 6380 
docker run -d --name redis-node3  -p 6381:6381 -p 16381:16381 redis:6.0.9 --cluster-enabled yes --cluster-config-file nodes.conf --port 6381 
docker run -d --name redis-node4  -p 6382:6382 -p 16382:16382 redis:6.0.9 --cluster-enabled yes --cluster-config-file nodes.conf --port 6382 
docker run -d --name redis-node5  -p 6383:6383 -p 16383:16383 redis:6.0.9 --cluster-enabled yes --cluster-config-file nodes.conf --port 6383 
docker run -d --name redis-node6  -p 6384:6384 -p 16384:16384 redis:6.0.9 --cluster-enabled yes --cluster-config-file nodes.conf --port 6384 

-------------

### redis集群搭建及遇到的问题
https://blog.csdn.net/qq_36850813/article/details/91353414

### 使用Docker Compose搭建高可用Redis集群
https://www.51cto.com/article/773181.html


### Cluster模式搭建
https://blog.csdn.net/weixin_60274825/article/details/121940080

###  docker-compose搭建redis集群--Cluster模式
https://www.cnblogs.com/brithToSpring/p/13187206.html
可参考

### docker-compose搭建redis集群
https://www.yoyoask.com/?p=6051


### 附录

version: '3'
 
services:
  redis-node1:
    image: redis:6.0.9
    container_name: redis1
    volumes:
      - ./redis.conf:/usr/local/etc/redis/redis.conf
    command: ["redis-server", "/usr/local/etc/redis/redis.conf"]
    ports:
      - "7001:6379"
      - "17001:17001"
 
  redis-node2:
    image: redis:6.0.9
    container_name: redis2
    volumes:
      - ./redis.conf:/usr/local/etc/redis/redis.conf
    command: ["redis-server", "/usr/local/etc/redis/redis.conf"]
    ports:
      - "7002:6379"
      - "17002:17002"

 
  redis-node3:
    image: redis:6.0.9
    container_name: redis3
    volumes:
      - ./redis.conf:/usr/local/etc/redis/redis.conf
    command: ["redis-server", "/usr/local/etc/redis/redis.conf"]
    ports:
      - "7003:6379"
      - "17003:17003"

 
  redis-node4:
    image: redis:6.0.9
    container_name: redis4
    volumes:
      - ./redis.conf:/usr/local/etc/redis/redis.conf
    command: ["redis-server", "/usr/local/etc/redis/redis.conf"]
    ports:
      - "7004:6379"
      - "17004:17004"

 
  redis-node5:
    image: redis:6.0.9
    container_name: redis5
    volumes:
      - ./redis.conf:/usr/local/etc/redis/redis.conf
    command: ["redis-server", "/usr/local/etc/redis/redis.conf"]
    ports:
      - "7005:6379"
      - "17005:17005"

 
  redis-node6:
    image: redis:6.0.9
    container_name: redis6
    volumes:
      - ./redis.conf:/usr/local/etc/redis/redis.conf
    command: ["redis-server", "/usr/local/etc/redis/redis.conf"]
    ports:
      - "7006:6379"
      - "17006:17006"



docker exec -it redis1 bash 


redis-cli --cluster create 192.168.200.46:7001 \
192.168.200.46:7002 \
192.168.200.46:7003 \
192.168.200.46:7004 \
192.168.200.46:7005 \
192.168.200.46:7006 \
--cluster-replicas 1 \
-a passwd123
--pass passwd123



docker run -d --name redis-node1  -p 6379:6379 -p 16379:16379 redis:6.0.9 --cluster-enabled yes --cluster-config-file nodes.conf --port 6379 
docker run -d --name redis-node2  -p 6380:6380 -p 16380:16380 redis:6.0.9 --cluster-enabled yes --cluster-config-file nodes.conf --port 6380 
docker run -d --name redis-node3  -p 6381:6381 -p 16381:16381 redis:6.0.9 --cluster-enabled yes --cluster-config-file nodes.conf --port 6381 
docker run -d --name redis-node4  -p 6382:6382 -p 16382:16382 redis:6.0.9 --cluster-enabled yes --cluster-config-file nodes.conf --port 6382 
docker run -d --name redis-node5  -p 6383:6383 -p 16383:16383 redis:6.0.9 --cluster-enabled yes --cluster-config-file nodes.conf --port 6383 
docker run -d --name redis-node6  -p 6384:6384 -p 16384:16384 redis:6.0.9 --cluster-enabled yes --cluster-config-file nodes.conf --port 6384 


redis-cli --cluster create 192.168.200.46:6379 \
192.168.200.46:6380 \
192.168.200.46:6381 \
192.168.200.46:6382 \
192.168.200.46:6383 \
192.168.200.46:6384 \
--cluster-replicas 1 \
-a passwd123
--pass passwd123

redis-cli -c -h 192.168.0.106 -p 7002 -a passwd123

redis-cli --cluster create 192.168.0.106:7001 \
192.168.0.106:7002 \
192.168.0.106:7003 \
192.168.0.106:7004 \
192.168.0.106:7005 \
192.168.0.106:7006 \
--cluster-replicas 1 \
--pass passwd123



version: '3'
 
services:
  redis-node1:
    image: redis:6.0.9
    container_name: redis1
    volumes:
      - ./redis.conf:/usr/local/etc/redis/redis.conf
    command: ["redis-server", "/usr/local/etc/redis/redis.conf"]
    ports:
      - "7001:6379"
      - "17001:16379"
 
  redis-node2:
    image: redis:6.0.9
    container_name: redis2
    volumes:
      - ./redis.conf:/usr/local/etc/redis/redis.conf
    command: ["redis-server", "/usr/local/etc/redis/redis.conf"]
    ports:
      - "7002:6379"
      - "17002:16379"

 
  redis-node3:
    image: redis:6.0.9
    container_name: redis3
    volumes:
      - ./redis.conf:/usr/local/etc/redis/redis.conf
    command: ["redis-server", "/usr/local/etc/redis/redis.conf"]
    ports:
      - "7003:6379"
      - "17003:16379"

 
  redis-node4:
    image: redis:6.0.9
    container_name: redis4
    volumes:
      - ./redis.conf:/usr/local/etc/redis/redis.conf
    command: ["redis-server", "/usr/local/etc/redis/redis.conf"]
    ports:
      - "7004:6379"
      - "17004:16379"

 
  redis-node5:
    image: redis:6.0.9
    container_name: redis5
    volumes:
      - ./redis.conf:/usr/local/etc/redis/redis.conf
    command: ["redis-server", "/usr/local/etc/redis/redis.conf"]
    ports:
      - "7005:6379"
      - "17005:16379"

 
  redis-node6:
    image: redis:6.0.9
    container_name: redis6
    volumes:
      - ./redis.conf:/usr/local/etc/redis/redis.conf
    command: ["redis-server", "/usr/local/etc/redis/redis.conf"]
    ports:
      - "7006:6379"
      - "17006:16379"





version: '3'
 
services:
  redis-node1:
    image: redis:6.0.9
    container_name: redis1
    volumes:
      - ./redis.conf:/usr/local/etc/redis/redis.conf
    command: ["redis-server", "/usr/local/etc/redis/redis.conf", "--cluster-announce-bus-port 17001"]
    ports:
      - "7001:6379"
      - "17001:16379"
    expose:
      - "8081"
 
  redis-node2:
    image: redis:6.0.9
    container_name: redis2
    volumes:
      - ./redis.conf:/usr/local/etc/redis/redis.conf
    command: ["redis-server", "/usr/local/etc/redis/redis.conf", "--cluster-announce-bus-port 17002"]
    ports:
      - "7002:6379"
      - "17002:16379"
    expose:
      - "8081"

 
  redis-node3:
    image: redis:6.0.9
    container_name: redis3
    volumes:
      - ./redis.conf:/usr/local/etc/redis/redis.conf
    command: ["redis-server", "/usr/local/etc/redis/redis.conf", "--cluster-announce-bus-port 17003"]
    ports:
      - "7003:6379"
      - "17003:16379"
    expose:
      - "8081"

 
  redis-node4:
    image: redis:6.0.9
    container_name: redis4
    volumes:
      - ./redis.conf:/usr/local/etc/redis/redis.conf
    command: ["redis-server", "/usr/local/etc/redis/redis.conf", "--cluster-announce-bus-port 17004"]
    ports:
      - "7004:6379"
      - "17004:16379"
    expose:
      - "8081"

 
  redis-node5:
    image: redis:6.0.9
    container_name: redis5
    volumes:
      - ./redis.conf:/usr/local/etc/redis/redis.conf
    command: ["redis-server", "/usr/local/etc/redis/redis.conf", "--cluster-announce-bus-port 17005"]
    ports:
      - "7005:6379"
      - "17005:16379"
    expose:
      - "8081"

 
  redis-node6:
    image: redis:6.0.9
    container_name: redis6
    volumes:
      - ./redis.conf:/usr/local/etc/redis/redis.conf
    command: ["redis-server", "/usr/local/etc/redis/redis.conf", "--cluster-announce-bus-port 17006"]
    ports:
      - "7006:6379"
      - "17006:16379"
    expose:
      - "8081"
      
---------------------------------------------

--- docker-compose.yml
version: '3'
 
services:
  redis-node1:
    image: redis:6.0.9
    container_name: redis1
    volumes:
      - ./redis.conf:/usr/local/etc/redis/redis.conf
    command: ["redis-server", "/usr/local/etc/redis/redis.conf"]
    ports:
      - "7001:6379"
      - "17001:17001"
 
  redis-node2:
    image: redis:6.0.9
    container_name: redis2
    volumes:
      - ./redis.conf:/usr/local/etc/redis/redis.conf
    command: ["redis-server", "/usr/local/etc/redis/redis.conf"]
    ports:
      - "7002:6379"
      - "17002:17002"

 
  redis-node3:
    image: redis:6.0.9
    container_name: redis3
    volumes:
      - ./redis.conf:/usr/local/etc/redis/redis.conf
    command: ["redis-server", "/usr/local/etc/redis/redis.conf"]
    ports:
      - "7003:6379"
      - "17003:17003"

 
  redis-node4:
    image: redis:6.0.9
    container_name: redis4
    volumes:
      - ./redis.conf:/usr/local/etc/redis/redis.conf
    command: ["redis-server", "/usr/local/etc/redis/redis.conf"]
    ports:
      - "7004:6379"
      - "17004:17004"

 
  redis-node5:
    image: redis:6.0.9
    container_name: redis5
    volumes:
      - ./redis.conf:/usr/local/etc/redis/redis.conf
    command: ["redis-server", "/usr/local/etc/redis/redis.conf"]
    ports:
      - "7005:6379"
      - "17005:17005"

 
  redis-node6:
    image: redis:6.0.9
    container_name: redis6
    volumes:
      - ./redis.conf:/usr/local/etc/redis/redis.conf
    command: ["redis-server", "/usr/local/etc/redis/redis.conf"]
    ports:
      - "7006:6379"
      - "17006:17006"

--- my.yml
version: '3'
 
services:
  redis-node1:
    image: redis:6.0.9
    container_name: redis1
    volumes:
      - ./redis.conf:/usr/local/etc/redis/redis.conf
    command: ["redis-server", "/usr/local/etc/redis/redis.conf"]
    ports:
      - "7001:6379"
 
  redis-node2:
    image: redis:6.0.9
    container_name: redis2
    volumes:
      - ./redis.conf:/usr/local/etc/redis/redis.conf
    command: ["redis-server", "/usr/local/etc/redis/redis.conf"]
    ports:
      - "7002:6379"
 
  redis-node3:
    image: redis:6.0.9
    container_name: redis3
    volumes:
      - ./redis.conf:/usr/local/etc/redis/redis.conf
    command: ["redis-server", "/usr/local/etc/redis/redis.conf"]
    ports:
      - "7003:6379"
 
  redis-node4:
    image: redis:6.0.9
    container_name: redis4
    volumes:
      - ./redis.conf:/usr/local/etc/redis/redis.conf
    command: ["redis-server", "/usr/local/etc/redis/redis.conf"]
    ports:
      - "7004:6379"
 
  redis-node5:
    image: redis:6.0.9
    container_name: redis5
    volumes:
      - ./redis.conf:/usr/local/etc/redis/redis.conf
    command: ["redis-server", "/usr/local/etc/redis/redis.conf"]
    ports:
      - "7005:6379"
 
  redis-node6:
    image: redis:6.0.9
    container_name: redis6
    volumes:
      - ./redis.conf:/usr/local/etc/redis/redis.conf
    command: ["redis-server", "/usr/local/etc/redis/redis.conf"]
    ports:
      - "7006:6379"

--- port.yml
version: '3'
 
services:
  redis-node1:
    image: redis:6.0.9
    container_name: redis1
    volumes:
      - ./redis.conf:/usr/local/etc/redis/redis.conf
    command: ["redis-server", "/usr/local/etc/redis/redis.conf", "--cluster-announce-bus-port 17001"]
    ports:
      - "7001:6379"
      - "17001:16379"
    expose:
      - "6379"
 
  redis-node2:
    image: redis:6.0.9
    container_name: redis2
    volumes:
      - ./redis.conf:/usr/local/etc/redis/redis.conf
    command: ["redis-server", "/usr/local/etc/redis/redis.conf", "--cluster-announce-bus-port 17002"]
    ports:
      - "7002:6379"
      - "17002:16379"
    expose:
      - "6379"

 
  redis-node3:
    image: redis:6.0.9
    container_name: redis3
    volumes:
      - ./redis.conf:/usr/local/etc/redis/redis.conf
    command: ["redis-server", "/usr/local/etc/redis/redis.conf", "--cluster-announce-bus-port 17003"]
    ports:
      - "7003:6379"
      - "17003:16379"
    expose:
      - "6379"

 
  redis-node4:
    image: redis:6.0.9
    container_name: redis4
    volumes:
      - ./redis.conf:/usr/local/etc/redis/redis.conf
    command: ["redis-server", "/usr/local/etc/redis/redis.conf", "--cluster-announce-bus-port 17004"]
    ports:
      - "7004:6379"
      - "17004:16379"
    expose:
      - "6379"

 
  redis-node5:
    image: redis:6.0.9
    container_name: redis5
    volumes:
      - ./redis.conf:/usr/local/etc/redis/redis.conf
    command: ["redis-server", "/usr/local/etc/redis/redis.conf", "--cluster-announce-bus-port 17005"]
    ports:
      - "7005:6379"
      - "17005:16379"
    expose:
      - "6379"

 
  redis-node6:
    image: redis:6.0.9
    container_name: redis6
    volumes:
      - ./redis.conf:/usr/local/etc/redis/redis.conf
    command: ["redis-server", "/usr/local/etc/redis/redis.conf", "--cluster-announce-bus-port 17006"]
    ports:
      - "7006:6379"
      - "17006:16379"
    expose:
      - "6379"


--- redis.conf
cluster-enabled yes
cluster-config-file nodes.conf
cluster-node-timeout 5000
appendonly yes
masterauth passwd123
requirepass passwd123
protected-mode no
port 6379
# bind 0.0.0.0
# cluster-announce-port 8081

------------------------------
docker-compose -f port.yml up -d
docker-compose down

搜索关键词
docker-compose安装redis cluster

