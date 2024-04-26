搭建spring security oauth2认证授权服务器
参考：https://blog.csdn.net/qq_36551991/article/details/134809089

前言
下面是在spring security项目的基础上搭建spring security oauth2认证授权服务器

1、添加依赖
spring security oauth2认证授权服务器主要需要以下依赖
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<!-- for OAuth 2.0 -->
<dependency>
    <groupId>org.springframework.security.oauth</groupId>
    <artifactId>spring-security-oauth2</artifactId>
    <version>{oauth2_version}</version>
</dependency>


Spring Security对OAuth2默认可访问端点

​/oauth/authorize​ ​​:申请授权码code，涉及类​ ​AuthorizationEndpoint​ ​
​/oauth/token​ ​​:获取令牌token，涉及类​ ​TokenEndpoint​ ​

​ ​/oauth/check_token​ ​​:用于资源服务器请求端点来检查令牌是否有效，涉及类​ ​CheckTokenEndpoint​ ​

​ ​/oauth/confirm_access​ ​​:用于确认授权提交，涉及类​ ​WhitelabelApprovalEndpoint​ ​

​ ​/oauth/error​ ​​:授权错误信息，涉及​ ​WhitelabelErrorEndpoint​ ​

​ ​/oauth/token_key​ ​​:提供公有密匙的端点，使用JWT令牌时会使用，涉及类​ ​TokenKeyEndpoint​


2、创建认证授权配置类
认证授权服务器最重要的就是认证授权配置类的配置

1、创建 ​一个自定义 ​类继承​ ​AuthorizationServerConfigurerAdapter​ ​
2、在​ ​自定义 ​类上添加注解​@Configuration​和​@EnableAuthorizationServer​注解
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;


    /**
     * 密码模式需要注入authenticationManager
     * @param endpoints
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.authenticationManager(authenticationManager);
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security
                .tokenKeyAccess("permitAll()")
                .checkTokenAccess("isAuthenticated()")
                //让/oauth/token支持client_id以及client_secret作登录认证
                .allowFormAuthenticationForClients();
    }

    /**
     * 客户端信息配置
     * @param clients
     * @throws Exception
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                //客户端ID
                .withClient("admin")
                //客户端密码
                .secret(passwordEncoder.encode("123123"))
                //配置访问token的有效期
                .accessTokenValiditySeconds(3600)
                //配置重定向的跳转，用于授权成功之后的跳转
                .redirectUris("http://www.baidu.com")
                //授权范围标识，哪部分资源可访问（all是标识，不是代表所有）
                .scopes("all")
                //授权模式, 可同时支持多种授权类型
                .authorizedGrantTypes("authorization_code", "password", "implicit","client_credentials","refresh_token")
                //true为自动批准，不需要用户手动点击授权，直接返回授权码
                .autoApprove(true);
    }




    @Bean
    public PasswordEncoder passwordEncoder(){
        return  new BCryptPasswordEncoder() ;
    }



}

3、创建安全配置类
    1、创建 ​一个自定义 ​类继承​ ​WebSecurityConfigurerAdapter​ ​
    2、在​ ​自定义 ​类上添加注解​@Configuration​和​@EnableWebSecurity​注解
/**
 * 配置Web应用程序的安全性，包括定义哪些URL需要被保护（即需要进行身份验证），以及如何进行身份验证等
 */
@Configuration
@EnableWebSecurity
public class OAuth2SecurityConfig  extends WebSecurityConfigurerAdapter {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //创建一个登录用户
        auth.inMemoryAuthentication()
                .withUser("admin")
                .password(passwordEncoder.encode("123123"))
                .authorities("admin_role");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //关闭CSRF
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/auth/**", "/login/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .permitAll();
    }



    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        // 采用密码授权模式需要显式配置AuthenticationManager
        return super.authenticationManagerBean();
    }

}


4、获取授权码路径及参数
获取授权码的请求url：/oauth/authorize

参数列表如下

client_id：客户端准入标识。

response_type：授权码模式固定为code。

scope：客户端权限。

grant_type：授权类型，填写authorization_code，表示授权码模式

code：授权码，就是刚刚获取的授权码，注意：授权码只使用一次就无效了，需要重新申请。

redirect_uri：跳转uri，当授权码申请成功后会跳转到此地址，并在后边带上code参数

1、使用以下地址申请授权码
http://localhost:8080/oauth/authorize?client_id=test&response_type=code&grant_type=authorization_code&scop=all&redirect_uri=http://www.baidu.com

这里实际要替换client_id，因为在服务器中配置了client_id是admin
http://localhost:8080/oauth/authorize?client_id=admin&response_type=code
上面是必须的参数
2、访问后，会让我们进行登录，登录成功之后就会跳转到我们的redirect_uri地址，并携带上了授权码

如下：fJf571就是我们的授权码

https://www.baidu.com/?code=fJf571
个人测试时的授权码是：XYlqUm

3、获取授权码之后，客户端就可以拿着授权码向授权服务器索要访问access_token

获取token的地址为：/oauth/token

参数列表如下：

client_id：客户端准入标识。
client_secret：客户端秘钥。
grant_type：授权类型，填写authorization_code，表示授权码模式
code：授权码，就是刚刚获取的授权码，注意：授权码只使用一次就无效了，需要重新申请。
redirect_uri：申请授权码时的跳转url，一定和申请授权码时用的redirect_uri一致
scope： 权限

postman测试
在authorization里添加一个basic auth，username为client_id，password为secret
在curl中，可设置为'Authorization: Basic YWRtaW46MTIzMTIz'，其中YWRtaW46MTIzMTIz是"用户名:密码"的base64编码，
即base64.encode("admin:123123")

curl -X POST http://localhost:8080/oauth/token \
 -H 'Authorization: Basic YWRtaW46MTIzMTIz' \
 -H 'Content-Type: application/x-www-form-urlencoded' \
 -d 'grant_type=authorization_code&code=XYlqUm&redirect_uri=http://www.baidu.com' -v

添加好认证信息之后，就可以请求接口了，下面就是授权模式下的获取token
还可以加上&scope=all参数
返回如下：
{"access_token":"d9f687eb-10d2-4133-8faa-3b22e4f1605b","token_type":"bearer","refresh_token":"ef74feb4-8fe9-485f-9a82-90051f787fbe","expires_in":3599,"scope":"all"}

密码模式

使用密码模式，主要是要注入authenticationManagerBean类

（1）资源拥有者将用户名、密码发送给客户端

（2）客户端拿着资源拥有者的用户名、密码向授权服务器请求令牌（access_token），请求如下：

使用密码模式，我只需要请求获取token的url：/oauth/token

参数列表如下：

参数 说明
client_id 客户端ID
client_secret 客户端秘钥。
grant_type 授权类型，填写password表示密码模式
username 资源拥有者用户名。
password 资源拥有者密码
scope 权限



#####
为什么@EnableOAuth2Sso被弃用？
@EnableOAuth2Sso注解是Spring Security OAuth2中的一个注解，用于启用单点登录（Single Sign-On，简称SSO）功能。它的作用是将当前应用程序配置为一个OAuth2客户端，以便与认证服务器进行交互，实现用户的身份验证和授权。

然而，@EnableOAuth2Sso注解在最新的Spring Security版本中已被弃用。原因如下：

维护性问题：随着OAuth2协议的发展和变化，Spring Security团队决定废弃@EnableOAuth2Sso注解，以提高代码的维护性和可扩展性。他们引入了新的注解@EnableOAuth2Client，用于替代@EnableOAuth2Sso注解。
更灵活的配置：@EnableOAuth2Client注解提供了更灵活的配置选项，可以满足更多场景的需求。它允许开发人员手动配置OAuth2客户端的行为，包括认证服务器的URL、客户端ID和密钥等。
统一的配置方式：@EnableOAuth2Client注解与其他Spring Security注解（如@EnableWebSecurity）的配置方式更加一致，使得整个应用程序的安全配置更加统一和易于理解。



###
1、背景
Spring Security OAuth(spring-security-oauth2)停更



主要意思是：生命周期终止通知 Spring Security OAuth(spring-security-oauth2)项目已达到生命周期结束，不再由VMware，Inc.积极维护。 此项目已被Spring Security和Spring Authorization Server提供的OAuth2支持所取代。
那么我们来分析一下这两个项目。Spring Authorization Server是Spring Security OAuth的进化版本，Spring Security OAuth官方已经宣布“End of Life”了。Spring Security OAuth使用的是OAuth2.0标准而Spring Authorization Serve引入了对OAuth 2.1和OpenID Connect 1.0规范的支持，并提供了更多功能和改进。它提供了OAuth 2.1和OpenID Connect 1.0规范以及其他相关规范的实现。它是基于Spring Security构建的，为构建OpenID Connect 1.0身份提供者和OAuth2授权服务器产品提供了安全、轻量级和可定制的基础。

OAuth 2.1和OpenID Connect 1.0是用于身份验证和授权的行业标准协议。它们被广泛应用于各种应用程序和系统，以实现安全的用户身份验证和授权流程。Spring Authorization Server提供了一个方便的方式来实现这些协议，使开发人员能够快速构建安全可靠的身份验证和授权系统。

2、区别


2.1、协议区别
Spring Security OAuth
Spring Security OAuth(spring-security-oauth2)是一个基于Spring Security的扩展，用于提供OAuth（开放授权）协议的实现。它提供了OAuth 2.0的客户端和服务端支持，使开发人员可以轻松地将OAuth集成到他们的应用程序中。

https://spring.io/projects/spring-security-oauth

Spring Authorization Server
Spring Authorization Server(spring-authorization-server)是一个框架，提供OAuth 2.1和OpenID Connect 1.0规范以及其他相关规范的实现。它构建在Spring Security之上，为构建OpenID Connect 1.0 Identity Providers和OAuth2 Authorization Server产品提供了一个安全，轻量级和可定制的基础。

https://spring.io/projects/spring-authorization-server​spring.io/projects/spring-authorization-server

补充知识点一：OAuth 2.0和OAuth 2.1区别

OAuth 2.0和OAuth 2.1是OAuth协议的两个不同版本，它们在以下方面有一些区别：

版本号：OAuth 2.0是最早发布的版本，而OAuth 2.1是OAuth 2.0的后续版本。
RFC规范：OAuth 2.0的规范定义在RFC 6749中，而OAuth 2.1的规范定义在RFC 8628中。OAuth 2.1是对OAuth 2.0规范的细化和改进。
安全性增强：OAuth 2.1在安全性方面引入了一些增强功能，以强化协议的安全性。这些增强功能包括：禁止使用基于浏览器的应用程序进行授权码授权流程（Authorization Code Flow with Proof Key for Code Exchange, PKCE），推荐使用强密码散列算法等。
便捷性：OAuth 2.1的目标之一是简化开发者对OAuth 2.0的实现和使用。它提供了更具可读性和易用性的规范，以减少实现和使用过程中的困惑和错误。
向后兼容性：OAuth 2.1被设计为向后兼容OAuth 2.0，这意味着现有的OAuth 2.0应用程序可以平滑地迁移到OAuth 2.1，而无需进行重大的更改。
需要注意的是，OAuth 2.1并非OAuth协议的完全新版本，而是在OAuth 2.0基础上的改进和细化。它旨在提供更好的安全性和便捷性，并解决了一些在OAuth 2.0中发现的问题。

在实际应用中，您应该根据具体需求和支持情况选择OAuth的版本。对于新的项目，建议考虑使用OAuth 2.1，以获得更好的安全性和简化的开发体验。对于现有的OAuth 2.0应用程序，可以考虑逐步迁移到OAuth 2.1以利用其优势和增强功能。

补充知识点二：OpenID Connect 1.0

OpenID Connect 1.0 是建立在 OAuth 2.0 协议之上的身份验证协议，它扩展了 OAuth 2.0 的功能，为客户端应用程序提供了更丰富的用户身份验证和属性信息获取能力。以下是 OpenID Connect 1.0 与 OAuth 2.0 的一些区别：

身份验证层：OAuth 2.0 主要关注授权方面，而 OpenID Connect 1.0 在 OAuth 2.0 的基础上添加了一个身份验证层，使得客户端应用程序可以直接获取用户的身份信息。
ID Token：OpenID Connect 1.0 引入了 ID Token 的概念，它是一个基于 JSON Web Token (JWT) 的数据结构，用于传递有关用户身份的信息。ID Token 包含有关用户的基本配置信息，如用户标识符、姓名、电子邮件地址等。OAuth 2.0 不包含类似的标准化方式来传递用户身份信息。
用户信息端点：OpenID Connect 1.0 定义了一个用户信息端点，客户端应用程序可以使用该端点来获取用户的详细配置信息。OAuth 2.0 并没有定义类似的端点。
标准化：OpenID Connect 1.0 是一项标准化的协议，规范了身份验证和用户信息获取的流程和数据格式。它定义了一组规范和约定，使得不同的身份提供商和客户端应用程序可以在遵循同样的标准下进行集成。OAuth 2.0 虽然也是一个标准化协议，但它主要关注授权方面，没有像 OpenID Connect 1.0 那样具体定义身份验证和用户信息获取的流程。
综上所述，OpenID Connect 1.0 是在 OAuth 2.0 的基础上增加了身份验证层和用户信息获取功能的协议。它通过引入 ID Token 和用户信息端点等机制，为客户端应用程序提供了更丰富的用户身份验证和属性信息获取能力。

2.2、认证模式区别
Spring Security OAuth
1）授权码模式：authorization_code

2）简化模式：implicit

3）客户端模式：client_credentials

4）密码模式：password

Spring Authorization Server


可以看到spring authorization server中password模式已经标记为过时了。另外多了jwt-bearer和device_code模式。



authorizationGrantTypes：客户端可以使用的授权授予类型。支持的值包括authorization_code、client_credentials、refresh_token和urn：ietf：params：oauth：grant-type：device_code。
密码模式已经被标记为过时了，官方推荐使用 JWT-Bearer Token 或 Device Code Flow 模式。

JWT-Bearer Token 模式是一种基于 JSON Web Token(JWT)的认证方式，客户端向服务器发送一个包含用户身份信息的 JWT Token,服务器通过验证该 Token 来确认用户的身份。这种模式的优点是简单易用、安全性高、跨平台性强。

Device Code Flow 模式则是一种基于移动设备的认证方式，客户端向服务器发送一个请求，要求获取一个一次性的访问码(Device Code),并在移动设备上完成该码的输入以完成身份验证。这种模式的优点是安全性高、易于集成和使用。

如果您正在使用 Spring Security 并且需要更改认证方式，请考虑使用 JWT-Bearer Token 或 Device Code Flow 模式。

https://github.com/spring-projects/spring-security/wiki/OAuth-2.0-Features-Matrix​github.com/spring-projects/spring-security/wiki/OAuth-2.0-Features-Matrix



### Spring Authorization Server
1.x 版本， 最低Java 17

https://docs.spring.io/spring-authorization-server/reference/getting-started.html

旧的Spring Security OAuth已停止维护，全面拥抱新解决方案Spring SAS
https://blog.csdn.net/zhangdaiscott/article/details/136389124


拥抱 Spring 全新 OAuth 解决方案  原文链接：https://blog.csdn.net/m0_68459853/article/details/125892628
 以下全文 Spring Authorization Server 简称为: SAS

背景
Spring 团队正式宣布 Spring Security OAuth 停止维护，该项目将不会再进行任何的迭代


目前 Spring 生态中的 OAuth2 授权服务器是 Spring Authorization Server 已经可以正式生产使用
作为 SpringBoot 3.0 的过渡版本 SpringBoot 2.7.0 过期了大量关于 SpringSecurity 的配置类，如沿用旧版本过期配置无法向上升级。
迁移过程
本文以PIG 微服务​开发平台为演示，适用于 Spring Security OAuth 2.3 <-> 2.5 的认证中心迁移

① Java 1.8 支持
目前最新的 SAS 0.3 基于 Java 11 构建，低版本 Java 无法使用

经过和 Spring Security 官方团队的沟通 0.3.1 将继续兼容 Java 1.8
我们联合 springboot 中文社区编译了适配 java 1.8 的版本坐标如下
  <dependency>
      <groupId>io.springboot.security</groupId>
      <artifactId>spring-security-oauth2-authorization-server</artifactId>
      <version>0.3.0</version>
  </dependency>
  
② 授权模式扩展


扩展支持密码模式，SAS 基于 oauth 2.1 协议不支持密码模式

扩展支持短信登录

③ Redis 令牌存储


官方目前没有提供基于 Redis 令牌持久化方案

PIG 扩展 PigRedisOAuth2AuthorizationService 支持

④ Token 输出格式化
使用自省令牌的情况下 默认实现为
ku4R4n7YD1f584KXj4k_3GP9o-HbdY-PDIIh-twPVJTmvHa5mLIoifaNhbBvFNBbse6_wAMcRoOWuVs9qeBWpxQ5zIFrF1A4g1Q7LhVAfH1vo9Uc7WL3SP3u82j0XU5x
为方便结合 redis 高效检索 token , 结合 RDM 分组也可以更方便的图形化观察
统一前缀::令牌类型::客户端ID::用户名::uuid
@Bean
public OAuth2TokenGenerator oAuth2TokenGenerator() {
  CustomeOAuth2AccessTokenGenerator accessTokenGenerator = new CustomeOAuth2AccessTokenGenerator();
  // 注入Token 增加关联用户信息
  accessTokenGenerator.setAccessTokenCustomizer(new CustomeOAuth2TokenCustomizer());
  return new DelegatingOAuth2TokenGenerator(accessTokenGenerator, new OAuth2RefreshTokenGenerator());
}
⑤ Token 输出增强
使用自省令牌，默认情况下输出的 Token 格式
{
    "access_token": "xx",
    "refresh_token": "xx",
    "scope": "server",
    "token_type": "Bearer",
    "expires_in": 43199
}
Token 增强输出关联用户信息
{
    "sub": "admin",
    "clientId": "test",
    "access_token": "xx",
    "refresh_token": "xx",
    "license": "https://pig4cloud.com",
    "user_info": {
        "username": "admin",
        "accountNonExpired": true,
        "accountNonLocked": true,
        "credentialsNonExpired": true,
        "enabled": true,
        "id": 1,
        "deptId": 1,
        "phone": "17034642999",
        "name": "admin",
        "attributes": {}
    }
}
⑥ 授权码模式个性化

⑦ 资源服务器
自省方案扩展支持资源资源服务器本地查询
扩展资源服务器本地自省
                        
