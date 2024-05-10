### 使用方法
https://blog.csdn.net/weixin_62319133/article/details/125615770

1.对象存储
Redisson将Redis中的字符串数据结构封装成了RBucket，通过RedissonClient的getBucket(key)方法获取一个RBucket对象实例，通过这个实例可以设置value或设置value和有效期。并且可以操作所有类型的对象。示例如下代码。
    /**
     * 通用对象桶，可以用来存放任类型的对象
     */
    @Test
    public void RedissonBucket(){
        //====================创建Redisson客户端====================
        Config config = new Config();
        //config.useSingleServer().setAddress("redis://127.0.0.1:6379").setPassword("123456");
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
        RedissonClient redissonClient = Redisson.create(config);
 
        //====================操作对象桶来存储对象(同步)====================
        RBucket<Object> bucket = redissonClient.getBucket("name");
        //设置值为victory，过期时间为3小时
        bucket.set("victory",30, TimeUnit.HOURS);
        Object value = bucket.get();
        System.out.println(value);
        //通过key取value值
        Object name = redissonClient.getBucket("name").get();
        System.out.println(name);
 
        //====================关闭客户端====================
        redissonClient.shutdown();
    }
    
2.二进制流存储
    /**
     * 二进制流
     * 提供了InputStream接口和OutputStream接口的实现
     */
    @Test
    public void RedissonStream() throws IOException {
        //====================创建Redisson客户端====================
        Config config = new Config();
        //config.useSingleServer().setAddress("redis://127.0.0.1:6379").setPassword("123456");
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
        RedissonClient redissonClient = Redisson.create(config);
 
        //====================操作流来存储对象====================
        RBinaryStream stream = redissonClient.getBinaryStream("stream");
        stream.set("name is ".getBytes());
        OutputStream outputStream = stream.getOutputStream();
 
        outputStream.write("victory".getBytes());
        InputStream inputStream = stream.getInputStream();
 
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] bytes = new byte[1024];
        int length;
        while ((length = inputStream.read(bytes)) != -1) {
            result.write(bytes, 0, length);
        }
        System.out.println(result.toString());
 
        //====================关闭客户端====================
        redissonClient.shutdown();
    }
   
    其中，stream set会重置binaryStream key对应的value值，stream.getOutputStream()的write则会在原先value后面追加内容
    
3.List
        /**
         * list
         * Redisson操作list
         */
        @Test
        public void list() {
            //====================创建Redisson客户端====================
            Config config = new Config();
            //config.useSingleServer().setAddress("redis://127.0.0.1:6379").setPassword("123456");
            config.useSingleServer().setAddress("redis://127.0.0.1:6379");
            RedissonClient redissonClient = Redisson.create(config);
     
            //====================操作list====================
            RList<String> list = redissonClient.getList("list");
            list.add("victory1");
            list.add("victory2");
            System.out.println(list);
            //取值
            List<Object> list1 = redissonClient.getList("list").get();
            System.out.println(list1);
            //移除索引0位置元素
            list.remove(0);
            System.out.println(list);
            //通过key取value值
            List<Object> list2 = redissonClient.getList("list").get();
            System.out.println(list2);
     
            //====================关闭客户端====================
            redissonClient.shutdown();
        }
        
4.Set
        /**
         * set
         * Redisson操作set
         */
        @Test
        public void set() {
            //====================创建Redisson客户端====================
            Config config = new Config();
            //config.useSingleServer().setAddress("redis://127.0.0.1:6379").setPassword("123456");
            config.useSingleServer().setAddress("redis://127.0.0.1:6379");
            RedissonClient redissonClient = Redisson.create(config);
     
            //====================操作list====================
            RSet<Object> set = redissonClient.getSet("set");
            set.add("victory1");
            set.add("victory2");
            System.out.println(set);
            //通过key取value值
            RSet<Object> set1 = redissonClient.getSet("set");
            System.out.println(set1);
     
            //====================关闭客户端====================
            redissonClient.shutdown();
        }

5.Map
Redisson将Redis中的字符串数据结构封装成了RMap，就是原本redis中的string类型
        /**
         * map
         * Redisson操作map
         * Redisson将Redis中的字符串数据结构封装成了RMap，就是原本redis中的string类型
         */
        @Test
        public void map() {
            //====================创建Redisson客户端====================
            Config config = new Config();
            //config.useSingleServer().setAddress("redis://127.0.0.1:6379").setPassword("123456");
            config.useSingleServer().setAddress("redis://127.0.0.1:6379");
            RedissonClient redissonClient = Redisson.create(config);
     
            //====================操作list====================
            RMap<Object, Object> map = redissonClient.getMap("map");
            map.put("name1","victory1");
            map.put("name2","victory2");
            map.forEach((key,value)->{
                System.out.println("key = "+key+" ,value = "+ value);
            });
     
            //通过key取value值
            Object o = redissonClient.getMap("map").get("name1");
            System.out.println(o);
     
            //====================关闭客户端====================
            redissonClient.shutdown();
        }
        
6.队列
        这里的队列和下面要说的发布订阅的功能跟MQ的不能说是毫无关系，只能说是一毛一样，计划这个专栏结束下个专栏专门来聊MQ，欢迎个位大佬交流指正
    /**
     * 队列
     * Redisson操作queue
     */
    @Test
    public void queue() {
        //====================创建Redisson客户端====================
        Config config = new Config();
        //config.useSingleServer().setAddress("redis://127.0.0.1:6379").setPassword("123456");
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
        RedissonClient redissonClient = Redisson.create(config);
 
        //====================操作queue====================
        RQueue<String> queue = redissonClient.getQueue("queue");
        //存值
        queue.add("victory1");
        queue.add("victory2");
        //取值
        String item = queue.poll();
        System.out.println(item);
 
        //
        RQueue<Object> queue1 = redissonClient.getQueue("queue");
        System.out.println(queue1);
 
        //====================关闭客户端====================
        redissonClient.shutdown();
    }
    
7.限流器
        基于Redis的分布式限流器可以用来在分布式环境下现在请求方的调用频率。既适用于不同Redisson实例下的多线程限流，也适用于相同Redisson实例下的多线程限流。该算法不保证公平性。
    /**
     * 限流器
     * Redisson操作rateLimiter
     */
    @Test
    public void rateLimiter() throws InterruptedException {
        //====================创建Redisson客户端====================
        Config config = new Config();
        //config.useSingleServer().setAddress("redis://127.0.0.1:6379").setPassword("123456");
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
        RedissonClient redissonClient = Redisson.create(config);
 
        //====================操作rateLimiter====================
        RRateLimiter rateLimiter = redissonClient.getRateLimiter("rateLimiter");
        //创建限流器，最大流速:每1秒钟产生20个令牌
        rateLimiter.trySetRate(RateType.OVERALL, 20, 1, RateIntervalUnit.SECONDS);
        for (int i = 0; i < 10; i++) {
            new Thread(new Runnable() {
                int i = 0;
                @Override
                public void run() {
                    while(true) {
                        rateLimiter.acquire(1);
                        System.out.println(Thread.currentThread() + "-" + System.currentTimeMillis() + "-" + i++);
                    }
                }
            }).start();
        }
        //等待执行完成,不设置等待可能出现还未执行完成客户端就关闭的情况
        Thread.sleep(5000);
 
        //====================关闭客户端====================
        redissonClient.shutdown();
    }
    
8.可重入锁
    /**
     * 可重入锁
     * Redisson操作RLock
     */
    @Test
    public void lock() throws InterruptedException {
        //====================创建Redisson客户端====================
        Config config = new Config();
        //config.useSingleServer().setAddress("redis://127.0.0.1:6379").setPassword("123456");
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
        RedissonClient redissonClient = Redisson.create(config);
 
        //====================操作lock====================
        RLock lock = redissonClient.getLock("lock");
        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                lock.lock();
                try {
                    System.out.println(Thread.currentThread() + "-" + System.currentTimeMillis() + "-" + "获取了锁");
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }
            }).start();
        }
        //等待执行完成,不设置等待可能出现还未执行完成客户端就关闭的情况
        Thread.sleep(5000);
 
        //====================关闭客户端====================
        redissonClient.shutdown();
    }
    
9.发布订阅
Redisson的分布式话题 RTopic 对象实现了发布、订阅的机制。
    /**
    *  发布订阅操作
    *  Redisson操作RTopic执行发布订阅操作
    **/
    @Test
    public void topicPublisherAndSubscriber() throws InterruptedException {
        //====================创建Redisson客户端====================
        Config config = new Config();
        //config.useSingleServer().setAddress("redis://127.0.0.1:6379").setPassword("123456");
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
        RedissonClient redissonClient = Redisson.create(config);
        RedissonClient redissonClient1 = Redisson.create(config);
 
        //====================操作topic执行发布操作====================
        RTopic topic1 = redissonClient.getTopic("topic",new SerializationCodec());
        topic1.publish(new Message(1L,"victory",18));
 
        //====================操作topic执行订阅操作====================
        Thread.sleep(5000);
        RTopic topic = redissonClient1.getTopic("topic", new SerializationCodec());
        topic.addListener(Message.class, new MessageListener<Message>() {
            @Override
            public void onMessage(CharSequence channel, Message msg) {
                System.out.println("onMessage:=========" + channel + "; Thread:========= " + Thread.currentThread().toString());
                System.out.println(" name : " + msg.getName() + " age : " + msg.getAge());
                LoggerFactory.getLogger(RedissonDemo.class).info("Redisson接收到消息",msg);
            }
        });
 
        //====================关闭客户端====================
        redissonClient.shutdown();
        redissonClient1.shutdown();
    }
    
10.布隆过滤器
        布隆过滤器是一个非常长的二进制向量和一系列随机哈希函数的组合，可用于检索一个元素是否存在。

        主要作用

        应对缓存穿透等场景，如果高并发一波冷数据的情况下，大量的请求可能会击穿我们的缓存导致了雪崩，此时我们可以设置布隆过滤器，如果索引在我们的过滤器中，请求来我们先去查内存，如果内存没有再去查DB，如果索引不存在于过滤器中我们快速返回失败来提高程序的性能。

原理如下：

        建立一个二进制向量，所有位设置0；
        选择K个散列函数，用于对元素进行K次散列，计算向量的位下标；
        添加元素：将K个散列函数作用于该元素，生成K个值作为位下标，将向量的对应位设置为1；
        检索元素：将K个散列函数作用于该元素，生成K个值作为位下标，若向量的对应位都是1，则说明该元素可能存在；否则，该元素肯定不存在；

        当然他也有他的局限性，比如误差率。随着存入的元素数量增加，误差率随之增加。但是如果元素数量太少，则使用散列表足矣。
    /**
     * Redisson利用Redis实现了Java分布式布隆过滤器（Bloom Filter）
     * 作用：在缓存层前添加布隆过滤器，常用于高并发场景下应对缓存穿透问题
     * 布隆过滤器是一个非常长的二进制向量和一系列随机哈希函数的组合，可用于检索一个元素是否存在；
     *
     */
    @Test
    public void bloomFilter() {
        //====================创建Redisson客户端====================
        Config config = new Config();
        //config.useSingleServer().setAddress("redis://127.0.0.1:6379").setPassword("123456");
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
        RedissonClient redissonClient = Redisson.create(config);
 
 
        //====================操作布隆过滤器====================
        RBloomFilter<String> bloomFilter = redissonClient.getBloomFilter("bloom-filter");
        // 初始化布隆过滤器，初始化预期插入的数据量为200，期望误差率为0.01
        bloomFilter.tryInit(200, 0.01);
        //插入数据
        bloomFilter.add("丁胜利");
        bloomFilter.add("丁向前");
        bloomFilter.add("胜利丁");
        //判断是否包含
        boolean victory = bloomFilter.contains("丁胜利");
        boolean forward = bloomFilter.contains("向前丁");
        System.out.println(victory); //true
        System.out.println(forward); //false
 
        //====================关闭客户端====================
        redissonClient.shutdown();
    }
    
 三.总结        
        Redisson相比于之前聊过的Jedis和Lettuce来说显得过于笨重，如果只是需要Redis实现简单的热点数据缓存和登录定时过期之类的小功能的话，建议在项目中慎重使用。
        
        
        
### [Java]Redission入门使用
https://blog.csdn.net/malu_record/article/details/133917370
一、什么是Redission
Redission is a Java-based distributed cache and object storage framework. It provides a variety of distributed data structures and services, such as lists, sets, queues, maps, bitmaps, HyperLogLog, geospatial indexing, etc.
It supports Redis protocol and JCache API, and provides many features such as real-time statistics, Pub/Sub, Lua script writing, etc., making it more convenient for applications to use and manage distributed data.
It also has advantages such as high performance, high reliability, and scalability.

Redission是一个Java实现的分布式缓存和分布式对象存储框架，它提供了许多分布式数据结构和服务，如列表、集合、队列、映射、位图、HyperLogLog、Geospatial索引等。
它支持Redis协议和JCache API，并提供了许多功能，如实时统计、Pub/Sub、Lua脚本编写等，使得应用程序可以更加方便地使用和管理分布式数据。
同时也具有高性能、高可靠性和可扩展性等优势。

Based on high-performance async and lock-free Java Redis client and Netty framework.

基于高性能异步和无锁的Java Redis Client 和Netty。

二、入门使用
1、Maven依赖
<dependency>
   <groupId>org.redisson</groupId>
   <artifactId>redisson</artifactId>
   <version>3.24.1</version>
</dependency>  

2、创建配置实例—— Create config object
Config config = new Config();
config.useClusterServers()
	// 集群状态扫描间隔时间，单位是毫秒
	.setScanInterval(2000)
	//cluster方式至少6个节点
	.addNodeAddress("redis://127.0.0.1:6379" )
	.addNodeAddress("redis://127.0.0.1:6380")
	.addNodeAddress("redis://127.0.0.1:6381")
	.addNodeAddress("redis://127.0.0.1:6382")
	.addNodeAddress("redis://127.0.0.1:6383")
	.addNodeAddress("redis://127.0.0.1:6384");

or read config from file

或者读取配置文件

Config config = Config.fromYAML(new File("config-file.yaml")); 

3、创建Redission实例—— Create Redisson instance
// Sync and Async API
RedissonClient redisson = Redisson.create(config);

4. Get Redis based implementation of java.util.concurrent.ConcurrentMap
RMap<Object, Object> map = redisson.getMap("map_key");
RLock rLock = map.getLock("key");

try {
	// lock 上锁
	rLock.lock();
    Object v = map.get(k);
    // todo 业务逻辑
} finally {
    rLock.unlock();
}

5. Get Redis based implementation of java.util.concurrent.locks.Lock
Redission加锁
设置过期时间为3秒的锁。
try {
	//获取锁对象
	RLock lock = redisson.getLock("my_key");
	//加锁
	lock.lock(3, TimeUnit.SECONDS);
	//todo 业务
} finally {
    lock.unlock();
}

若不设置过期时间，默认过期时间30秒，并触发Watchdog进行续期
try {
	//获取锁对象
	RLock lock = redisson.getLock("my_key");
	//加锁，默认过期30秒，并触发Watchdog进行续期
	lock.lock();
	//todo 业务
} finally {
    lock.unlock();
}

Watch Dog 的自动延期机制
如果一个拿到锁的线程正常执行任务但还没完成，那么看门狗会延长锁超时时间，锁不会因为超时而被释放。

看门狗默认续期时间是30s，可以通过修改Config.lockWatchdogTimeout指定时间。

如果使用lock方法传入过期时间leaseTime，那么超时自动解锁，不会触发Watch Dog延长时间。

6. Get Redis based implementation of java.util.concurrent.ExecutorService
RScheduledExecutorService executorService = redisson.getExecutorService("my_key");
//3秒时间后执行任务
executorService.schedule(()->{
    //todo 业务逻辑
},3,TimeUnit.SECONDS);

//2秒后执行，每3秒执行一次
executorService.scheduleAtFixedRate(()->{
   //业务逻辑
}, 2,3,TimeUnit.SECONDS);

//2秒后执行，每3秒执行一次
executorService.scheduleWithFixedDelay(()->{
   //业务逻辑
}, 2,3,TimeUnit.SECONDS);

7、等等…

### github和其它参考
https://github.com/redisson/redisson/wiki/%E7%9B%AE%E5%BD%95

https://blog.51cto.com/u_14691/7528876

### redission客户端获取所有key
https://blog.51cto.com/u_16213454/10177918

### redis三个连接客户端框架的选择：Jedis,Redisson,Lettuce
https://blog.csdn.net/w1014074794/article/details/88827946

Jedis api 在线网址：http://tool.oschina.net/uploads/apidocs/redis/clients/jedis/Jedis.html

redisson 官网地址：https://redisson.org/

redisson git项目地址：https://github.com/redisson/redisson

lettuce 官网地址：https://lettuce.io/

lettuce git项目地址：https://github.com/lettuce-io/lettuce-core

首先，在spring boot2之后，对redis连接的支持，默认就采用了lettuce。这就一定程度说明了lettuce 和Jedis的优劣。

概念：

Jedis：是老牌的Redis的Java实现客户端，提供了比较全面的Redis命令的支持，

Redisson：实现了分布式和可扩展的Java数据结构。

Lettuce：高级Redis客户端，用于线程安全同步，异步和响应使用，支持集群，Sentinel，管道和编码器。

优点：
　　Jedis：比较全面的提供了Redis的操作特性

Redisson：促使使用者对Redis的关注分离，提供很多分布式相关操作服务，例如，分布式锁，分布式集合，可通过Redis支持延迟队列

Lettuce：基于Netty框架的事件驱动的通信层，其方法调用是异步的。Lettuce的API是线程安全的，所以可以操作单个Lettuce连接来完成各种操作

可伸缩：

Jedis：使用阻塞的I/O，且其方法调用都是同步的，程序流需要等到sockets处理完I/O才能执行，不支持异步。Jedis客户端实例不是线程安全的，所以需要通过连接池来使用Jedis。

Redisson：基于Netty框架的事件驱动的通信层，其方法调用是异步的。Redisson的API是线程安全的，所以可以操作单个Redisson连接来完成各种操作

Lettuce：基于Netty框架的事件驱动的通信层，其方法调用是异步的。Lettuce的API是线程安全的，所以可以操作单个Lettuce连接来完成各种操作

lettuce能够支持redis4，需要java8及以上。
lettuce是基于netty实现的与redis进行同步和异步的通信。

lettuce和jedis比较：
jedis使直接连接redis server,如果在多线程环境下是非线程安全的，这个时候只有使用连接池，为每个jedis实例增加物理连接 ；

lettuce的连接是基于Netty的，连接实例（StatefulRedisConnection）可以在多个线程间并发访问，StatefulRedisConnection是线程安全的，所以一个连接实例可以满足多线程环境下的并发访问，当然这也是可伸缩的设计，一个连接实例不够的情况也可以按需增加连接实例。

Redisson实现了分布式和可扩展的Java数据结构，和Jedis相比，功能较为简单，不支持字符串操作，不支持排序、事务、管道、分区等Redis特性。Redisson的宗旨是促进使用者对Redis的关注分离，从而让使用者能够将精力更集中地放在处理业务逻辑上。

总结：
优先使用Lettuce，如果需要分布式锁，分布式集合等分布式的高级特性，添加Redisson结合使用，因为Redisson本身对字符串的操作支持很差。
