package com.fsyy.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
public class DefaultSecurityConfig {

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests(
                authorizeRequests ->
                        authorizeRequests
                                .antMatchers("/password/*")
                                .permitAll()
                                .anyRequest()
                                .authenticated())
                .formLogin(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    UserDetailsService users() {
        UserDetails user =
                User.builder().username("fsyy").password("{noop}123456").roles("USER").build();
        return new InMemoryUserDetailsManager(user);
    }

    /**
     curl 'http://localhost:8080/oauth2/authorize?client_id=baidu&client_secret=baidu&response_type=code' \
     -H 'Authorization: Basic ZnN5eToxMjM0NTY=' \
     -H 'Content-Type: application/x-www-form-urlencoded' \
     -H 'Cookie: ajs_anonymous_id=7bbac691-94c9-49dc-afd4-da1903c37989; wp-settings-time-1=1694676356; JSESSIONID=245433C90B2A465526A7E96CA9F29CAD' \
     -v

     curl -X POST http://localhost:8080/login \
     -H 'Authorization: Basic YWRtaW46MTIzMTIz' \
     -H 'Cookie: ajs_anonymous_id=7bbac691-94c9-49dc-afd4-da1903c37989; wp-settings-time-1=1694676356; JSESSIONID=245433C90B2A465526A7E96CA9F29CAD' \
     -L 'http://localhost:8080/oauth2/authorize?client_id=baidu&client_secret=baidu&response_type=code'
     -H 'Content-Type: application/x-www-form-urlencoded' \
     -d 'grant_type=authorization_code&code=XYlqUm&redirect_uri=http://www.baidu.com' -v


     curl 'http://localhost:8080/oauth2/authorize?client_id=baidu&client_secret=baidu&response_type=code' \
     -H 'Authorization: Basic ZnN5eToxMjM0NTY=' \
     -H 'Content-Type: application/x-www-form-urlencoded' \
     -H 'Cookie: ajs_anonymous_id=7bbac691-94c9-49dc-afd4-da1903c37989; wp-settings-time-1=1694676356; JSESSIONID=245433C90B2A465526A7E96CA9F29CAD' \
     -v


     curl 'http://localhost:8080/oauth2/authorize?client_id=baidu&client_secret=baidu&response_type=code' \
     -H 'Content-Type: application/x-www-form-urlencoded' \
     -H 'Cookie: ajs_anonymous_id=7bbac691-94c9-49dc-afd4-da1903c37989; wp-settings-time-1=1694676356; JSESSIONID=245433C90B2A465526A7E96CA9F29CAD' \
     -v

     Location: https://www.baidu.com?code=nFSJU4sMvuCuGmyj73peW1rt85nVoIK4o0KyyYmJotCPfe5a2g4E4YNZLRtCa_vNfmxIRTYhZtPawclQy70AdPefpOj7vd2LepVkxcOCUmLIYTFJU07n3QjeXSJwrCga

     code=nFSJU4sMvuCuGmyj73peW1rt85nVoIK4o0KyyYmJotCPfe5a2g4E4YNZLRtCa_vNfmxIRTYhZtPawclQy70AdPefpOj7vd2LepVkxcOCUmLIYTFJU07n3QjeXSJwrCga

     获取令牌：
     curl --location --request POST 'http://localhost:8080/oauth2/token' \
     --header 'Authorization: Basic YmFpZHU6YmFpZHU=' \
     --header 'Content-Type: application/x-www-form-urlencoded' \
     --data-urlencode 'grant_type=authorization_code' \
     --data-urlencode 'code=A5w2jSJWhgYNgF8UDyU8gJIGaoOyjQ0CGhAIkxsd9y24aZ7mzFw-eaCg026pSbGwQSBKtVE-s6wPizzTJ4j2UNXOsLwA58bsv9ou5MXmlrPFOfpml-sFHCBaVErqLdnd' \
     --data-urlencode 'redirect_uri=https://www.baidu.com' \
     -v


     curl 'http://localhost:8080/oauth2/authorize?client_id=baidu&client_secret=baidu&response_type=code' \
     -H 'Content-Type: application/x-www-form-urlencoded' \
     -H 'Cookie: ajs_anonymous_id=7bbac691-94c9-49dc-afd4-da1903c37989; wp-settings-time-1=1694676356; JSESSIONID=133328F0473557BCFD5E2E18A2F3F5C5' \
     -v

     {"access_token":"6wBcaWQ4Hqx7jeY7UpapKGyjUrFZosqwos8g0htJeBkbH_1oA1i68iGkVhzCE75cerwhwUkcL7J1ethmUzcDjYVPQ0sdd0W-1SPRHKNoydG4uc5o8onCKgqtEHz1LORe","refresh_token":"E4xHMAx48YydOTMmlC8k_phJO2cpYQQnRPrQXzg_ZBnNZCwY-KlzMfr4t9CJkXIOINbWhFdbkF08G1vG-Z-bVBk0nz0-7P0qFE29Nc-9hmQ6NwWR1GO-A8ZEW0T1sv-M","token_type":"Bearer","expires_in":300}


     刷新令牌：
     curl --location --request POST 'http://localhost:8080/oauth2/token' \
     --header 'Authorization: Basic YmFpZHU6YmFpZHU=' \
     --header 'Content-Type: application/x-www-form-urlencoded' \
     --data-urlencode 'grant_type=refresh_token' \
     --data-urlencode 'refresh_token=E4xHMAx48YydOTMmlC8k_phJO2cpYQQnRPrQXzg_ZBnNZCwY-KlzMfr4t9CJkXIOINbWhFdbkF08G1vG-Z-bVBk0nz0-7P0qFE29Nc-9hmQ6NwWR1GO-A8ZEW0T1sv-M' \
     -v


     {"access_token":"06Z66VceYbM1TnsmZR09DtvYVPU0Slr9UBDep6DgYrMy6Kod6cMRinzMFwRhx-tDsTycroElVAEAUqAXw6xVliFHN3mkecK2UCudWgvnHhklc3vXysH05lGWPQXMGoMw","refresh_token":"E4xHMAx48YydOTMmlC8k_phJO2cpYQQnRPrQXzg_ZBnNZCwY-KlzMfr4t9CJkXIOINbWhFdbkF08G1vG-Z-bVBk0nz0-7P0qFE29Nc-9hmQ6NwWR1GO-A8ZEW0T1sv-M","token_type":"Bearer","expires_in":300}


     撤销令牌
        通过 access_token
     curl --location --request POST 'http://localhost:8080/oauth2/revoke' \
     --header 'Authorization: Basic YmFpZHU6YmFpZHU=' \
     --header 'Content-Type: application/x-www-form-urlencoded' \
     --data-urlencode 'token=F4ZR5L0BIrfolBh7v65rxvEv1cr5R6URtTftLBO-dYbpwzQQbokUW9TXeWLj3rcYWLHNnBPupIafdPZ8EOEQc50V7bxfXPh-ZmaBTnfBtrjqyVm3GsbLqdG7tY8295Rj' \
     --data-urlencode 'token_type_hint=access_token'

        通过 refresh_token
     curl --location --request POST 'http://localhost:8080/oauth2/revoke' \
     --header 'Authorization: Basic YmFpZHU6YmFpZHU=' \
     --header 'Content-Type: application/x-www-form-urlencoded' \
     --data-urlencode 'token=ZaOys78E8VqESI9wAY5ISD2JWbPhOELnpICeiO6vPcE7bJik7qG-NWI59hXW6uw2lSJjrYzrxzFqohBk0TpA28Mj1_i030JiYniAf9hNWPt5iN0dld6J40y3fKfD_7Uq' \
     --data-urlencode 'token_type_hint=refresh_token'


     */


    /**
     * OAuth2AuthorizationCodeRequestAuthenticationProvider
     * OAuth2AuthorizationEndpointFilter
     */
}
