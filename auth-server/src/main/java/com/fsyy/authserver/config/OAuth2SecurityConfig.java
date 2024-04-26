package com.fsyy.authserver.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;

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



