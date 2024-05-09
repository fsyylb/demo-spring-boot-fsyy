### https://cloud.tencent.com/developer/article/2277231
Redis 的3大 Java 客户端组件
Redis 的3大 Java 客户端组件
Redis 官方推荐的 Java 客户端有Jedis、lettuce 和 Redisson。

客户端组件1：Jedis
Jedis 是老牌的 Redis 的 Java 实现客户端，提供了比较全面的 Redis 命令的支持、

Jedis 在线网址：http://tool.oschina.net/uploads/apidocs/redis/clients/jedis/Jedis.html

优点：

支持全面的 Redis 操作特性（可以理解为API比较全面）。
缺点：

使用阻塞的 I/O，且其方法调用都是同步的，程序流需要等到 sockets 处理完 I/O 才能执行，不支持异步；
Jedis 客户端实例不是线程安全的，所以需要通过连接池来使用 Jedis。
客户端组件2：Redisson
Redisson 是一个在 Redis 的基础上实现的 Java 驻内存数据网格（In-Memory Data Grid）。

Redisson 提供了使用Redis 的最简单和最便捷的方法。

它不仅提供了一系列的分布式的 Java 常用对象，还提供了许多分布式服务。

其中包括：

BitSet, Set, Multimap, SortedSet, Map, List, Queue, BlockingQueue, Deque, BlockingDeque, Semaphore, Lock, AtomicLong, CountDownLatch, Publish / Subscribe, Bloom filter, Remote service, Spring cache, Executor service, Live Object service, Scheduler service)

Redisson 的宗旨是促进使用者对Redis的关注分离（Separation of Concern），从而让使用者能够将精力更集中地放在处理业务逻辑上。

redisson 官网地址：https://redisson.org/

redisson git项目地址：https://github.com/redisson/redisson

优点：

使用者对 Redis 的关注分离，可以类比 Spring 框架，这些框架搭建了应用程序的基础框架和功能，提升开发效率，让开发者有更多的时间来关注业务逻辑；
提供很多分布式相关操作服务，例如，分布式锁，分布式集合，可通过Redis支持延迟队列等。
Redisson基于Netty框架的事件驱动的通信层，其方法调用是异步的。
Redisson的API是线程安全的，所以可以操作单个Redisson连接来完成各种操作
缺点：

Redisson 对字符串的操作支持比较差。
客户端组件3：lettuce
lettuce （[ˈletɪs]），是一种可扩展的线程安全的 Redis 客户端，支持异步模式。

如果避免阻塞和事务操作，如BLPOP和MULTI/EXEC，多个线程就可以共享一个连接。

lettuce 底层基于 Netty，支持高级的 Redis 特性，比如哨兵，集群，管道，自动重新连接和Redis数据模型。

lettuce能够支持redis4，需要java8及以上。

lettuce是基于netty实现的与redis进行同步和异步的通信。

lettuce 官网地址：https://lettuce.io/

lettuce git项目地址：https://github.com/lettuce-io/lettuce-core

优点：

支持同步异步通信模式；
Lettuce 的 API 是线程安全的，如果不是执行阻塞和事务操作，如BLPOP和MULTI/EXEC，多个线程就可以共享一个连接。
lettuce、jedis、Redisson 三者比较
jedis使直接连接redis server,如果在多线程环境下是非线程安全的，这个时候只有使用连接池，为每个jedis实例增加物理连接；

lettuce的连接是基于Netty的，连接实例（StatefulRedisConnection）可以在多个线程间并发访问，StatefulRedisConnection是线程安全的，所以一个连接实例可以满足多线程环境下的并发访问，当然这也是可伸缩的设计，一个连接实例不够的情况也可以按需增加连接实例。

Jedis 和 lettuce 是比较纯粹的 Redis 客户端，几乎没提供什么高级功能。

Jedis 的性能比较差，所以如果你不需要使用 Redis 的高级功能的话，优先推荐使用 lettuce。

Redisson实现了分布式和可扩展的Java数据结构，和Jedis相比，功能较为简单，不支持字符串操作，不支持排序、事务、管道、分区等Redis特性。

Redisson的宗旨是促进使用者对Redis的关注分离，从而让使用者能够将精力更集中地放在处理业务逻辑上。

如果需要分布式锁，分布式集合等分布式的高级特性，添加Redisson结合使用，因为Redisson本身对字符串的操作支持很差。

Redisson 的优势是提供了很多开箱即用的 Redis 高级功能，如果你的应用中需要使用到 Redis 的高级功能，建议使用 Redisson。

具体 Redisson 的高级功能可以参考：https://redisson.org/

使用建议
建议：lettuce + Redisson

在spring boot2之后，redis连接默认就采用了lettuce。

就想 spring  的本地缓存，默认使用Caffeine一样，

这就一定程度说明了，lettuce 比 Jedis在性能的更加优秀。


### https://zhuanlan.zhihu.com/p/570109949
Jedis,Redisson,Lettuce 三者的区别
共同点：都提供了基于 Redis 操作的 Java API，只是封装程度，具体实现稍有不同。

不同点：

1.1、Jedis
是 Redis 的 Java 实现的客户端。支持基本的数据类型如：String、Hash、List、Set、Sorted Set。

特点：使用阻塞的 I/O，方法调用同步，程序流需要等到 socket 处理完 I/O 才能执行，不支持异步操作。Jedis 客户端实例不是线程安全的，需要通过连接池来使用 Jedis。

1.1、Redisson
优点点：分布式锁，分布式集合，可通过 Redis 支持延迟队列。

1.3、 Lettuce
用于线程安全同步，异步和响应使用，支持集群，Sentinel，管道和编码器。

基于 Netty 框架的事件驱动的通信层，其方法调用是异步的。Lettuce 的 API 是线程安全的，所以可以操作单个 Lettuce 连接来完成各种操作。


### https://developer.huawei.com/consumer/cn/blog/topic/03825550899620047
Redis客户端Jedis、Lettuce 和 Redisson优缺点总结
Redis 官方推荐的 Java 客户端有Jedis、Lettuce 和 Redisson。本文总结这些客户端的优缺点

1. Jedis
Jedis 是老牌的 Redis 的 Java 实现客户端，提供了比较全面的 Redis 命令的支持，其官方网址是：https://github.com/redis/jedis

优点：

支持全面的 Redis 操作特性（可以理解为API比较全面）。
老牌的redis客户端，使用很稳定; spring-boot 1.x 默认使用jedis;
缺点：

使用阻塞的 IO，且其方法调用都是同步的，程序流需要等到 sockets 处理完 IO 才能执行，不支持异步；
Jedis 客户端实例不是线程安全的，所以需要通过连接池来使用 Jedis。
2. Lettuce
Lettuce是一种可扩展的线程安全的 Redis 客户端，支持异步模式。如果避免阻塞和事务操作，如BLPOP和MULTIEXEC，多个线程就可以共享一个连接。Lettuce 底层基于 Netty，支持高级的 Redis 特性，比如哨兵，集群，管道，自动重新连接和Redis数据模型。Lettuce 的官网地址是：https://github.com/lettuce-io/lettuce-core

优点：

支持同步异步通信模式；
Lettuce 的 API 是线程安全的，如果不是执行阻塞和事务操作，如BLPOP和MULTIEXEC，多个线程就可以共享一个连接；
spring-boot 2.x 默认使用Lettuce;
3. Redisson
Redisson 是一个在 Redis 的基础上实现的 Java 驻内存数据网格（In-Memory Data Grid）。它不仅提供了一系列的分布式的 Java 常用对象，还提供了许多分布式服务。其中包括( BitSet, Set, Multimap, SortedSet, Map, List, Queue, BlockingQueue, Deque, BlockingDeque, Semaphore, Lock, AtomicLong, CountDownLatch, Publish / Subscribe, Bloom filter, Remote service, Spring cache, Executor service, Live Object service, Scheduler service) Redisson 提供了使用Redis 的最简单和最便捷的方法。Redisson 的宗旨是促进使用者对Redis的关注分离（Separation of Concern），从而让使用者能够将精力更集中地放在处理业务逻辑上。Redisson的官方网址是：https://redisson.org/

优点：

使用者对 Redis 的关注分离，可以类比 Spring 框架，这些框架搭建了应用程序的基础框架和功能，提升开发效率，让开发者有更多的时间来关注业务逻辑；
提供很多分布式相关操作服务，例如，分布式锁，分布式集合，可通过Redis支持延迟队列等。
缺点：

Redisson 对字符串的操作支持比较差。
4. 使用建议
一般是采用如下搭配组合。

Jedis + Redisson
Lettuce + Redisson