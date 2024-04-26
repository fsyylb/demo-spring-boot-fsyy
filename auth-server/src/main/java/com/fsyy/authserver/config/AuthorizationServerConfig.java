package com.fsyy.authserver.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;

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
                //授权范围标识，哪部分资源可访问（all是标识，不是代表所有）  //XYlqUm
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


/**

 curl -X POST http://localhost:8080/oauth/token \
 -H 'Authorization: Basic YWRtaW46MTIzMTIz' \
 -H 'Content-Type: application/x-www-form-urlencoded' \
 -d 'grant_type=authorization_code&code=XYlqUm&redirect_uri=http://www.baidu.com' -v

 {"access_token":"d9f687eb-10d2-4133-8faa-3b22e4f1605b","token_type":"bearer","refresh_token":"ef74feb4-8fe9-485f-9a82-90051f787fbe","expires_in":3599,"scope":"all"}

 */
}

