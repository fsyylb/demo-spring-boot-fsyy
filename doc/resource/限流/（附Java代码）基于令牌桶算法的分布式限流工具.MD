一、开场白
最近在使用SpringCloudGateway构建项目网关，处理限流的过程中发现gateway提供了一种基于令牌桶的分布式限流实现，非常感兴趣，于是在经过一番处理，从gateway的源码中提取出一个轻量的基于令牌桶算法的分布式限流工具，供参考选用。
SpringCloudGateway将限流的核心实现放在lua脚本中，使用Redis存储限流配置数据，同时利用Redis对lua脚本的良好支持，实现一个高效的令牌桶限流工具。
二、核心限流脚本
限流脚本命名为：simple_request_rate_limiter.lua，放置到：META-INF/script目录下，脚本内容如下：

ini复制代码local tokens_key = KEYS[1]
local timestamp_key = KEYS[2]

local rate = tonumber(ARGV[1])
local capacity = tonumber(ARGV[2])
local now = tonumber(ARGV[3])
local requested = tonumber(ARGV[4])

local fill_time = capacity/rate
local ttl = math.floor(fill_time*2)

local last_tokens = tonumber(redis.call("get", tokens_key))
if last_tokens == nil then
  last_tokens = capacity
end

local last_refreshed = tonumber(redis.call("get", timestamp_key))
if last_refreshed == nil then
  last_refreshed = 0
end

local delta = math.max(0, now-last_refreshed)
local filled_tokens = math.min(capacity, last_tokens+(delta*rate))
local allowed = filled_tokens >= requested
local new_tokens = filled_tokens
local allowed_num = 0
if allowed then
  new_tokens = filled_tokens - requested
  allowed_num = 1
end

redis.call("setex", tokens_key, ttl, new_tokens)
redis.call("setex", timestamp_key, ttl, now)

return { allowed_num, new_tokens }


三、Java端核心实现类
我们项目基于Java语言开发，且深度的依赖Spring，我们使用Spring-Data-Redis实现对Redis的操作，具体的代码实现如下：

arduino复制代码public class SimpleRedisRateLimiter {

    private static final Logger LOG = LoggerFactory.getLogger(SimpleRedisRateLimiter.class);

    private static final String RATE_LIMITER_PREFIX = "cn:bbhorse:rate:limiter:{";
    private static final String RAL_TOKENS_SUFFIX = "}:tokens:";
    private static final String RAL_TIMESTAMP_SUFFIX = "}:timestamp:";
    private static final String RAL_DEFAULT_TOKEN_ACQUIRE = "1";

    private StringRedisTemplate redis;

    private RedisScript<List<Long>> script;

    public SimpleRedisRateLimiter(StringRedisTemplate redis, RedisScript<List<Long>> script) {
        this.redis = redis;
        this.script = script;
    }

    public boolean isAllowed(int replenishRate, int burstCapacity, String sourceId) {
        try {
            String tokenKey = RATE_LIMITER_PREFIX + sourceId + RAL_TOKENS_SUFFIX;
            String timestampKey = RATE_LIMITER_PREFIX + sourceId + RAL_TIMESTAMP_SUFFIX;
            List<String> scriptKeys = Arrays.asList(tokenKey, timestampKey);
            List<Long> ret = redis.execute(
                                            script,
                                            scriptKeys, String.valueOf(replenishRate),
                                            String.valueOf(burstCapacity),
                                            String.valueOf(Instant.now().getEpochSecond()),
                                            RAL_DEFAULT_TOKEN_ACQUIRE);
            return 1 == ret.get(0);
        } catch (Exception ex) {
            /*
             * We don't want a hard dependency on Redis to allow traffic. Make sure to set
             * an alert so you know if this is happening too much. Stripe's observed
             * failure rate is 0.01%.
             */
            LOG.error("Error determining if user allowed from redis", ex);
        }
        return true;
    }
}


四、配置创建组件实例
我们从META-INF/script目录下加载lua脚本，并生成我们的限流实现（SimpleRedisRateLimiter的实例）。代码实现如下：

less复制代码@Configuration
@ConditionalOnClass({ RedisTemplate.class })
public class RateLimitConfig {

    @Bean
    public RedisScript simpleRateLimiterScript() {
        DefaultRedisScript redisScript = new DefaultRedisScript<>();
        redisScript.setScriptSource(
                new ResourceScriptSource(
                new ClassPathResource("META-INF/script/simple_request_rate_limiter.lua")));
        redisScript.setResultType(List.class);
        return redisScript;
    }

    @Bean
    public SimpleRedisRateLimiter simpleRedisRateLimiter(
            @Qualifier("stringRedisTemplate") StringRedisTemplate redis,
            @Qualifier("simpleRateLimiterScript") RedisScript<List<Long>> script) {
        return new SimpleRedisRateLimiter(redis, script);
    }
}


五、限流组件应用
在外部业务类中注入限流组件的bean（simpleRedisRateLimiter），即可使用限流方法，参考代码如下：

typescript复制代码@Component
public class SimpleRedisRateLimiterTutorial {

    @Resource
    private SimpleRedisRateLimiter simpleRedisRateLimiter;

    ......
    public void turorial() {
        ......
        // 每1s允许访问资源5次
        if (!simpleRedisRateLimiter.isAllowed(5, 5, "tutorialResouceId")) {
            throw new RuntimeException("Too frequent visits!");
        }
        ......
    }
}


六、总结
通过对SpringCloudGateway限流源码的阅读与提取，我们得到了一款轻量级的分布式限流工具，可以作为独立组件，非常方便的应用到类似需要限流的业务场景中。
End.

参考链接：https://juejin.cn/post/6874806267638988808
