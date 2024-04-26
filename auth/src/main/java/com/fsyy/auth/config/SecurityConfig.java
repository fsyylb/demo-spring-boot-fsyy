/*
package com.fsyy.auth.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.authorization.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.OAuth2TokenFormat;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.authorization.InMemoryOAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.InMemoryOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.ClientSettings;
import org.springframework.security.oauth2.server.authorization.config.ProviderSettings;
import org.springframework.security.oauth2.server.authorization.config.TokenSettings;
import org.springframework.security.oauth2.server.authorization.token.DelegatingOAuth2TokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.JwtGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2AccessTokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2RefreshTokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Configuration
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    */
/*
    open id
     *//*

    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }
    */
/*
    open id
     *//*


    @Bean
    public JwtEncoder jwtEncoder() {
        return new NimbusJwtEncoder(jwkSource());
    }

    @Bean
    public OAuth2TokenGenerator<?> tokenGenerator() {
        JwtGenerator jwtGenerator = new JwtGenerator(jwtEncoder());
        jwtGenerator.setJwtCustomizer(jwtCustomizer());
        OAuth2AccessTokenGenerator accessTokenGenerator = new OAuth2AccessTokenGenerator();
        OAuth2RefreshTokenGenerator refreshTokenGenerator = new OAuth2RefreshTokenGenerator();
        return new DelegatingOAuth2TokenGenerator(jwtGenerator, accessTokenGenerator, refreshTokenGenerator);
    }

    */
/**
     * 端点的 Spring Security 过滤器链
     * @param http
     * @return
     * @throws Exception
     *//*

    @Bean
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http)
            throws Exception {


        OAuth2AuthorizationServerConfigurer<HttpSecurity> authorizationServerConfigurer =
                new OAuth2AuthorizationServerConfigurer<>();

        */
/*
        open id
         *//*

        authorizationServerConfigurer
                .oidc(oidc -> {
                            // 用户信息
                            oidc.userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint.userInfoMapper(oidcUserInfoAuthenticationContext -> {
                                String username = oidcUserInfoAuthenticationContext.getAuthorization().getPrincipalName();
                                String sql = "select url from oauth_demo.oauth2_user where username = ?";
                                */
/*UserEntity userEntity = jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(UserEntity.class), username);
                                Optional<UserEntity> userEntityOptional = Optional.ofNullable(userEntity);
                                Map<String, Object> claims  = new HashMap<>();
                                if (userEntityOptional.isPresent()) {
                                    claims.put("url", userEntity.getUrl());
                                }*//*

                                Map<String, Object> claims  = new HashMap<>();
                                claims.put("uri", "uri");
                                claims.put("sub", username);
                                return new OidcUserInfo(claims);
                            }));
                            // 客户端注册
                            oidc.clientRegistrationEndpoint(Customizer.withDefaults());
                        }
                );

        http.oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt);
        */
/*
        open id
         *//*


        RequestMatcher endpointsMatcher = authorizationServerConfigurer
                .getEndpointsMatcher();

        http
                .requestMatcher(endpointsMatcher)
                .authorizeRequests(authorizeRequests ->
                        authorizeRequests.anyRequest().authenticated()
                )
                //.userDetailsService(userService)
                .csrf(AbstractHttpConfigurer::disable)
                .apply(authorizationServerConfigurer);


        //未通过身份验证时重定向到登录页面授权端点
        http.exceptionHandling((exceptions) -> exceptions
                .authenticationEntryPoint(
                        new LoginUrlAuthenticationEntryPoint("/login"))
        );

        return http.build();
    }


    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer() {
        return context -> {
            JwsHeader.Builder headers = context.getHeaders();
            JwtClaimsSet.Builder claims = context.getClaims();
            Map<String, Object> map = claims.build().getClaims();
            if (context.getTokenType().getValue().equals(OidcParameterNames.ID_TOKEN)) {
                // Customize headers/claims for access_token
//                headers.header("customerHeader", "这是一个自定义header");
//                claims.claim("customerClaim", "这是一个自定义Claim");
                */
/*String username = (String) map.get("sub");
                String sql = "select avatar, url from oauth_demo.oauth2_user where username = ?";
                UserEntity userEntity = jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(UserEntity.class), username);
                Optional<UserEntity> userEntityOptional = Optional.ofNullable(userEntity);
                if (userEntityOptional.isPresent()) {
                    claims.claim("url", userEntityOptional.get().getUrl());
                    claims.claim("avatar", userEntityOptional.get().getAvatar());
                }*//*

                claims.claim("url", "url");
                claims.claim("avatar", "avatar");
            }
        };
    }

    */
/**
     * 用于身份验证的 Spring Security 过滤器链
     * @param http
     * @return
     * @throws Exception
     *//*

    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http)
            throws Exception {
        http
                .authorizeHttpRequests((authorize) -> authorize
                        .anyRequest().authenticated()
                )
                //表单登录处理从授权服务器过滤器链
                .formLogin(Customizer.withDefaults());

        return http.build();
    }

    */
/**
     * 返回注册客户端资源，注意这里采用的是内存模式，后续可以改成jdbc模式。RegisteredClientRepository用于管理客户端的实例。
     * @return
     *//*

    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        RegisteredClient registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("messaging-client")
                .clientSecret(passwordEncoder().encode("secret"))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .redirectUri("http://www.baidu.com")
                .redirectUri("http://127.0.0.1:8080/login/oauth2/code/messaging-client-oidc")
                .scope(OidcScopes.OPENID)
                .scope("message.read")
                .scope("message.write")
                .clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
                .tokenSettings(TokenSettings.builder()
                        // token有效期100分钟
                        .accessTokenTimeToLive(Duration.ofMinutes(100L))
                        // 使用默认JWT相关格式
                        .accessTokenFormat(OAuth2TokenFormat.SELF_CONTAINED)
                        // 开启刷新token
                        .reuseRefreshTokens(true)
                        // refreshToken有效期120分钟
                        .refreshTokenTimeToLive(Duration.ofMinutes(120L))
                        .idTokenSignatureAlgorithm(SignatureAlgorithm.RS256).build()
                )
                .build();

//        JdbcRegisteredClientRepository registeredClientRepository = new JdbcRegisteredClientRepository(jdbcTemplate);
//        RegisteredClient client = registeredClientRepository.findByClientId("messaging-client");
//        Optional<RegisteredClient> clientOptional = Optional.ofNullable(client);
//        if (clientOptional.isEmpty()) {
//            registeredClientRepository.save(registeredClient);
//        }
//        return registeredClientRepository;
        return new InMemoryRegisteredClientRepository(registeredClient);
    }

    @Bean
    public OAuth2AuthorizationService authorizationService() {
        //return new JdbcOAuth2AuthorizationService(jdbcTemplate, registeredClientRepository());
        return new InMemoryOAuth2AuthorizationService();
    }

    */
/**
     * 授权确认信息处理服务
     *//*

    @Bean
    public OAuth2AuthorizationConsentService authorizationConsentService() {
        //return new JdbcOAuth2AuthorizationConsentService(jdbcTemplate, registeredClientRepository());
        return new InMemoryOAuth2AuthorizationConsentService();
    }


    */
/**
     * 生成jwk资源,com.nimbusds.jose.jwk.source.JWKSource用于签署访问令牌的实例。
     * @return
     *//*

    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        KeyPair keyPair = generateRsaKey();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return new ImmutableJWKSet<>(jwkSet);
    }

    */
/**
     * 生成密钥对,启动时生成的带有密钥的实例java.security.KeyPair用于创建JWKSource上述内容
     * @return
     *//*

    private static KeyPair generateRsaKey() {
        KeyPair keyPair;
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            keyPair = keyPairGenerator.generateKeyPair();
        }
        catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
        return keyPair;
    }

    */
/**
     * ProviderSettings配置 Spring Authorization Server的实例
     * @return
     *//*

    @Bean
    public ProviderSettings providerSettings() {
        return ProviderSettings.builder().build();
    }
}*/
