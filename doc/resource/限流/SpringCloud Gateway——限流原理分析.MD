参考：https://juejin.cn/post/7154016365458751518

SpringCloud Gateway——限流原理分析
1. SpringCloud Gateway中限流使用
这里以路由ID为限流维度，Redis做限流逻辑，即每个路由ID在1秒内可以接收多少个请求。

pom.xml

<dependencies>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-gateway</artifactId>
    </dependency>

    <dependency>
        <groupId>com.alibaba.cloud</groupId>
        <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
    </dependency>

    <!-- redis限流 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-redis</artifactId>
    </dependency>
</dependencies>
application.yml

spring:
  application:
    name: nacos-gateway
  cloud:
    nacos:
      discovery:
        server-addr: 127.0.0.1
        namespace: 405e877e-56ab-4755-8dd8-6541e1ee4845
        group: nacos-dev
        username: nacos
        password: nacos
    gateway:
      discovery:
        locator:
          enabled: true
      routes:
        - id: app
          uri: lb://nacos-app
          predicates:
            - Path=/app/**
          filters:
            - StripPrefix=1
            - name: RequestRateLimiter
              args:
                redis-rate-limiter.replenishRate: 10
                redis-rate-limiter.burstCapacity: 10
                key-resolver: "#{@routeIdKeyResolver}"

  redis:
    host: 127.0.0.1
    port: 6379
RateLimitConfig: 实例化RouteIdKeyResolver路由ID维度解析器

/**
 * @author Tarzan写bug
 * @since 2022/10/13
 */
@Configuration
public class RateLimitConfig {

    @Bean(value = "routeIdKeyResolver")
    public KeyResolver routeIdKeyResolver() {
        return exchange -> {
            Route route = (Route) exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
            return Mono.just(route.getId());
        };
    }
}
2. 源码分析
1. 主流程
请求到达RequestRateLimiterGatewayFilterFactory；
通过KeyResolver解析出限流维度ID；
通过redis lua脚本判断是否允许访问。
2. 过滤器RequestRateLimiterGatewayFilterFactory
启动Gateway时会初始化RequestRateLimiterGatewayFilterFactory

GatewayAutoConfiguration

@Bean
@ConditionalOnBean({ RateLimiter.class, KeyResolver.class })
public RequestRateLimiterGatewayFilterFactory requestRateLimiterGatewayFilterFactory(
    RateLimiter rateLimiter, KeyResolver resolver) {
    return new RequestRateLimiterGatewayFilterFactory(rateLimiter, resolver);
}
从@ConditionalOnBean中可以看出初始化这个过滤器需要RateLimiter和KeyResolver两个关键类，后面会分析这两个类。

根据XxxGatewayFilterFactory结构，核心逻辑在apply()中

RequestRateLimiterGatewayFilterFactory

@SuppressWarnings("unchecked")
@Override
public GatewayFilter apply(Config config) {
    KeyResolver resolver = getOrDefault(config.keyResolver, defaultKeyResolver);
    RateLimiter<Object> limiter = getOrDefault(config.rateLimiter,
                                               defaultRateLimiter);
    boolean denyEmpty = getOrDefault(config.denyEmptyKey, this.denyEmptyKey);
    HttpStatusHolder emptyKeyStatus = HttpStatusHolder
        .parse(getOrDefault(config.emptyKeyStatus, this.emptyKeyStatusCode));

    // 调用KeyResolver.resolve()解析出限流的维度
    return (exchange, chain) -> resolver.resolve(exchange).defaultIfEmpty(EMPTY_KEY)
        .flatMap(key -> {
            // 没找到则通过过滤器
            if (EMPTY_KEY.equals(key)) {
                if (denyEmpty) {
                    setResponseStatus(exchange, emptyKeyStatus);
                    return exchange.getResponse().setComplete();
                }
                return chain.filter(exchange);
            }
            String routeId = config.getRouteId();
            if (routeId == null) {
                Route route = exchange
                    .getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
                routeId = route.getId();
            }
            // 调用RateLimiter.isAllowed()判断是否允许访问
            return limiter.isAllowed(routeId, key).flatMap(response -> {

                // 返回的response中的header加入到exchange header中
                for (Map.Entry<String, String> header : response.getHeaders()
                     .entrySet()) {
                    exchange.getResponse().getHeaders().add(header.getKey(),
                                                            header.getValue());
                }

                // 允许访问则通过过滤器
                if (response.isAllowed()) {
                    return chain.filter(exchange);
                }

                // 被限流，返回HTTP状态码429
                setResponseStatus(exchange, config.getStatusCode());
                return exchange.getResponse().setComplete();
            });
        });
}
首先通过KeyResolver.resolve()解析出限流的维度ID，然后调用RateLimiter.isAllowed()判断是否被限制访问。

3. KeyResolver
该接口是用来解析限流的维度的，这些维度包括路由ID、请求IP、请求路径等。如果没有自定义维度，Gateway提供了默认的实现PrincipalNameKeyResolver，这里自定义实现了以路由ID为维度的解析器。主要就是从Route获取ID.

@Bean(value = "routeIdKeyResolver")
public KeyResolver routeIdKeyResolver() {
    return exchange -> {
        Route route = (Route) exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
        return Mono.just(route.getId());
    };
}
4. RedisRateLimiter
RedisRateLimiter是RateLimiter的一种实现，通过redis lua脚本来实现限流算法。首先看看何时初始化的？

GatewayRedisAutoConfiguration

@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter(RedisReactiveAutoConfiguration.class)
@AutoConfigureBefore(GatewayAutoConfiguration.class)
@ConditionalOnBean(ReactiveRedisTemplate.class)
@ConditionalOnClass({ RedisTemplate.class, DispatcherHandler.class })
class GatewayRedisAutoConfiguration {

    @Bean
    @SuppressWarnings("unchecked")
    public RedisScript redisRequestRateLimiterScript() {
        DefaultRedisScript redisScript = new DefaultRedisScript<>();
        redisScript.setScriptSource(new ResourceScriptSource(
            new ClassPathResource("META-INF/scripts/request_rate_limiter.lua")));
        redisScript.setResultType(List.class);
        return redisScript;
    }

    @Bean
    @ConditionalOnMissingBean
    public RedisRateLimiter redisRateLimiter(ReactiveStringRedisTemplate redisTemplate,
                                             @Qualifier(RedisRateLimiter.REDIS_SCRIPT_NAME) RedisScript<List<Long>> redisScript,
                                             ConfigurationService configurationService) {
        return new RedisRateLimiter(redisTemplate, redisScript, configurationService);
    }

}
只有引入redis依赖才会初始化这个配置类，在GatewayRedisAutoConfiguration中出初始化两个Bean，一个是读取META-INF/scripts/request_rate_limiter.lua路径下的lua脚本，另外一个就是初始化RedisRateLimiter.

RedisRateLimiter

@Override
@SuppressWarnings("unchecked")
public Mono<Response> isAllowed(String routeId, String id) {
    if (!this.initialized.get()) {
        throw new IllegalStateException("RedisRateLimiter is not initialized");
    }

    // 获取限流配置
    Config routeConfig = loadConfiguration(routeId);

    // 生产速率，每秒生产多少个令牌
    int replenishRate = routeConfig.getReplenishRate();

    // 最大访问数量
    int burstCapacity = routeConfig.getBurstCapacity();

    // 每个请求消耗多个个令牌
    int requestedTokens = routeConfig.getRequestedTokens();

    try {
        // 根据限流维度得出redis key
        List<String> keys = getKeys(id);

        // lua脚本参数
        List<String> scriptArgs = Arrays.asList(replenishRate + "",
                                                burstCapacity + "", Instant.now().getEpochSecond() + "",
                                                requestedTokens + "");
        // 运行lua脚本
        Flux<List<Long>> flux = this.redisTemplate.execute(this.script, keys,
                                                           scriptArgs);
        return flux.onErrorResume(throwable -> {
            if (log.isDebugEnabled()) {
                log.debug("Error calling rate limiter lua", throwable);
            }
            return Flux.just(Arrays.asList(1L, -1L));
        }).reduce(new ArrayList<Long>(), (longs, l) -> {
            // 从lua返回两个返回值，是否允许访问和剩余令牌数量
            // 将结果存入List中
            longs.addAll(l);
            return longs;
        }).map(results -> {
            boolean allowed = results.get(0) == 1L;
            Long tokensLeft = results.get(1);

            // 构造Response返回
            Response response = new Response(allowed,
                                             getHeaders(routeConfig, tokensLeft));

            if (log.isDebugEnabled()) {
                log.debug("response: " + response);
            }
            return response;
        });
    }
    catch (Exception e) {
        log.error("Error determining if user allowed from redis", e);
    }
    return Mono.just(new Response(true, getHeaders(routeConfig, -1L)));
}
该方法调用redis lua脚本实现限流逻辑，那我们来看看lua脚本的逻辑

META-INF/scripts/request_rate_limiter.lua

--redis key名，用于保存限流维度下剩余令牌数量，request_rate_limiter.{id}.tokens
local tokens_key = KEYS[1]
--redis key名，用于保存限流维度下最近获取令牌时间，request_rate_limiter.{id}.timestamp
local timestamp_key = KEYS[2]
--redis.log(redis.LOG_WARNING, "tokens_key " .. tokens_key)

--生产速率，每秒生产多少个令牌
local rate = tonumber(ARGV[1])
--容量
local capacity = tonumber(ARGV[2])
--当前时间（秒级时间戳）
local now = tonumber(ARGV[3])
--每个请求消耗的令牌个数
local requested = tonumber(ARGV[4])

--填充时间=容量/生产速率
local fill_time = capacity/rate
--key过期时间设置为填充时间的2倍
local ttl = math.floor(fill_time*2)

--剩余令牌数量
local last_tokens = tonumber(redis.call("get", tokens_key))
--不存在key，则初始化令牌数量为最大容量
if last_tokens == nil then
  last_tokens = capacity
end

--最近获取令牌秒级时间戳
local last_refreshed = tonumber(redis.call("get", timestamp_key))
--不存在key，则last_refreshed = 0
if last_refreshed == nil then
  last_refreshed = 0
end

--距离上次获取令牌时间相差多少秒
local delta = math.max(0, now-last_refreshed)
--计算当前令牌数量（考虑delta时间内生成的令牌个数=delta*速率）
local filled_tokens = math.min(capacity, last_tokens+(delta*rate))
--当前令牌数量是否大于1
local allowed = filled_tokens >= requested
local new_tokens = filled_tokens

local allowed_num = 0
--允许访问，新令牌数量-1，allowed_num=1
if allowed then
  new_tokens = filled_tokens - requested
  allowed_num = 1
end


--保存令牌个数和最近获取令牌时间
if ttl > 0 then
  redis.call("setex", tokens_key, ttl, new_tokens)
  redis.call("setex", timestamp_key, ttl, now)
end

return { allowed_num, new_tokens }
流程图：

lua脚本1.png

lua脚本2.png

限流逻辑的关键点在计算当前令牌的个数，要考虑间隔时间内生产出的令牌个数：

local filled_tokens = math.min(capacity, last_tokens+(delta*rate))
这里为什么要取最小值呢？因为有一种场景是上一秒的令牌没有用完，这时会导致last_tokens+(delta*rate)>最大容量，导致限流不准确。这里这样做的目的是，不管上一秒令牌数量是否用完，在下一秒的令牌个数都是初始容量。

这里可能会考虑一秒内的滑动窗口问题，其实这里不会考虑毫秒级，因为RedisRateLimiter传参到lua脚本时用到是Instant.now().getEpochSecond()秒级时间戳，所以并发的时候只有同一秒或差几秒的说法，这里还要提醒的就是秒级时间戳相减后的结果就是等于相差了几秒。





参考：https://juejin.cn/post/7276446768459628602
Spring Gateway限流源码解析

Spring Gateway限流
Spring Gateway的限流是通过过滤器来实现的，这个过滤器首先会从配置中拿到

Key解析器
限流器
是否拒绝空Key
空Key的状态码
// Key解析器
KeyResolver resolver = getOrDefault(config.keyResolver, defaultKeyResolver);
// 限流器
RateLimiter<Object> limiter = getOrDefault(config.rateLimiter, defaultRateLimiter);
// 是否拒绝空Key
boolean denyEmpty = getOrDefault(config.denyEmptyKey, this.denyEmptyKey);
// 拒绝空Key的状态码的Holder
HttpStatusHolder emptyKeyStatus = HttpStatusHolder
    .parse(getOrDefault(config.emptyKeyStatus, this.emptyKeyStatusCode));
说白了就是下面这些配置的值：

spring:
  cloud:
    gateway:
      routes:
      - id: requestratelimiter_route
        uri: https://example.org
        filters:
        - name: RequestRateLimiter
          args:
            # 补充令牌的速度
            redis-rate-limiter.replenishRate: 10
            # 令牌桶容量
            redis-rate-limiter.burstCapacity: 20
            # 一次请求消耗的令牌数
            redis-rate-limiter.requestedTokens: 1
当然，如果用户没有配置，那么就会拿到默认值。

ServerWebExchange
首先得了解ServerWebExchange是HTTP请求-响应交互的约定。

它提供对HTTP请求和响应的访问，还公开其他与服务器端处理相关的属性和功能，如请求属性。

也就是对一次请求-响应进行了封装。

Key解析器
KeyResolver用于解析请求该请求的Key，也就是这个请求的唯一标识，不过一个请求的唯一标识可以有很多，可以是他的ip，也可以是他的某个请求头比如token，所以这里将Key解析器抽象为一个接口：

public interface KeyResolver {
	Mono<String> resolve(ServerWebExchange exchange);
}
返回Mono是因为在Spring WebFlux 中，响应通常是异步的，这意味着它们不会立即返回，而是需要一些时间来处理，而Gateway基于Spring WebFlux，所以自然也是异步的，从而需要返回一个Mono来获取结果。

Spring默认提供了一个PrincipalNameKeyResolver的实现类，它将每一次请求的Principal的name，也就是该请求已验证用户的name属性来充当key。

public class PrincipalNameKeyResolver implements KeyResolver {
	public static final String BEAN_NAME = "principalNameKeyResolver";

	@Override
	public Mono<String> resolve(ServerWebExchange exchange) {
		return exchange.getPrincipal().flatMap(p -> Mono.justOrEmpty(p.getName()));
	}
}
限流器
限流器是整个Gateway实现限流的核心，它规定了一个用于判断改接口是否被允许的方法：

public interface RateLimiter<C> extends StatefulConfigurable<C> {
    // 判断某一条路由规则的某一个请求是否被允许
    // routeId: 路由规则的唯一标识
    // id: 请求的唯一标识 -> 也就是用KeyResolver解析出来的Key
	Mono<Response> isAllowed(String routeId, String id);
	
    // 封装的返回值
	class Response {
        // 请求是否被允许
        private final boolean allowed;
        // 令牌的剩余量
		private final long tokensRemaining;
        // 响应的请求头
		private final Map<String, String> headers;
        // ...
	}
}
Spring提供了一个默认的实现类RedisRateLimiter，这个限流器是采用Redis + Lua脚本利用令牌桶的方式来实现限流的，首先它会从配置中拿到三个参数，分别对应着：

令牌填充速率
桶容量
每次请求消耗令牌数
// How many requests per second do you want a user to be allowed to do?
int replenishRate = routeConfig.getReplenishRate();

// How much bursting do you want to allow?
int burstCapacity = routeConfig.getBurstCapacity();

// How many tokens are requested per request?
int requestedTokens = routeConfig.getRequestedTokens();
由于令牌桶的逻辑是在Redis中进行的，所以需要在Redis执行Lua脚本，而之所以要在Redis层面实现，是因为这样可以减少频繁地访问Redis从而因为频繁的网络请求导致性能损耗。

在执行Redis的Lua脚本之前，首先需要获取参数的Keys和Args列表：

List<String> keys = getKeys(id);

// The arguments to the LUA script. time() returns unixtime in seconds.
List<String> scriptArgs = Arrays.asList(replenishRate + "", burstCapacity + "", "", requestedTokens + "");

// allowed, tokens_left = redis.eval(SCRIPT, keys, args)
Flux<List<Long>> flux = this.redisTemplate.execute(this.script, keys, scriptArgs);
其中getKeys()的具体实现如下：

static List<String> getKeys(String id) {
    // use `{}` around keys to use Redis Key hash tags
    // this allows for using redis cluster

    // Make a unique key per user.
    String prefix = "request_rate_limiter.{" + id;

    // You need two Redis keys for Token Bucket.
    String tokenKey = prefix + "}.tokens";
    String timestampKey = prefix + "}.timestamp";
    return Arrays.asList(tokenKey, timestampKey);
}
不难看出最后能得到两个Redis的Key，假设ID为1：

request_rate_limiter.{1}.tokens
request_rate_limiter.{1}.timestamp
其中request_rate_limiter.{1}.tokens存储的是令牌的数量

而request_rate_limiter.{1}.timestamp则存的是上一次请求的时间戳

令牌桶算法
令牌桶算法是想象有一个桶，桶里会存放令牌，请求需要通过，首先得拿到指定数量的令牌才能通过，当然，桶里会源源不断地补充令牌。

假设桶的容量是capacity，而补充令牌的速度为rate，允许通过的令牌数为1个，那么可以写出如下伪代码：

capacity = 20
rate = 10
nowTime = now()
NEED_TOKEN_NUM = 1

lastTime = getLastTime()
// 获取剩余的令牌
remainingTokens = getRemainingTokens()
// 上次剩余的加上上次到这次之间补充的
tokens = remainingTokens + rate * (lastTime - nowTime)
// 不得超过最大容量
tokens = min(tokens, capacity)
if (tokens >= NEED_TOKEN_NUM) {
    // 取得需要的令牌
    tokens -= NEED_TOKEN_NUM
    // 设置上一次时间为当前时间
    setLastTime(nowTime)
    return true
} else {
    return false
}
Gateway的Lua脚本如下：

redis.replicate_commands()

local tokens_key = KEYS[1]
local timestamp_key = KEYS[2]
--redis.log(redis.LOG_WARNING, "tokens_key " .. tokens_key)

local rate = tonumber(ARGV[1])
local capacity = tonumber(ARGV[2])
local now = redis.call('TIME')[1]
local requested = tonumber(ARGV[4])

local fill_time = capacity/rate
local ttl = math.floor(fill_time*2)

--redis.log(redis.LOG_WARNING, "rate " .. ARGV[1])
--redis.log(redis.LOG_WARNING, "capacity " .. ARGV[2])
--redis.log(redis.LOG_WARNING, "now " .. now)
--redis.log(redis.LOG_WARNING, "requested " .. ARGV[4])
--redis.log(redis.LOG_WARNING, "filltime " .. fill_time)
--redis.log(redis.LOG_WARNING, "ttl " .. ttl)

local last_tokens = tonumber(redis.call("get", tokens_key))
if last_tokens == nil then
  last_tokens = capacity
end
--redis.log(redis.LOG_WARNING, "last_tokens " .. last_tokens)

local last_refreshed = tonumber(redis.call("get", timestamp_key))
if last_refreshed == nil then
  last_refreshed = 0
end
--redis.log(redis.LOG_WARNING, "last_refreshed " .. last_refreshed)

local delta = math.max(0, now-last_refreshed)
local filled_tokens = math.min(capacity, last_tokens+(delta*rate))
local allowed = filled_tokens >= requested
local new_tokens = filled_tokens
local allowed_num = 0
if allowed then
  new_tokens = filled_tokens - requested
  allowed_num = 1
end

--redis.log(redis.LOG_WARNING, "delta " .. delta)
--redis.log(redis.LOG_WARNING, "filled_tokens " .. filled_tokens)
--redis.log(redis.LOG_WARNING, "allowed_num " .. allowed_num)
--redis.log(redis.LOG_WARNING, "new_tokens " .. new_tokens)

if ttl > 0 then
  redis.call("setex", tokens_key, ttl, new_tokens)
  redis.call("setex", timestamp_key, ttl, now)
end

-- return { allowed_num, new_tokens, capacity, filled_tokens, requested, new_tokens }
return { allowed_num, new_tokens }
其中值得注意的是TTL的设置是根据capacity/rate来确定的，说白了就是桶被填满的时间。

是否拒绝空Key
如果denyEmpty为true也就是配置了拒绝空Key则会在后面当解析出的Key为空的时候直接拒绝请求，否则不限流直接放行：

if (EMPTY_KEY.equals(key)) {
if (denyEmpty) {
    setResponseStatus(exchange, emptyKeyStatus);
    return exchange.getResponse().setComplete();
}
	return chain.filter(exchange);
}