package com.fsyy.ssetouch.core.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import io.swagger.annotations.ApiOperation;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

import java.util.Collections;
import java.util.List;

@EnableKnife4j
@Configuration
@EnableSwagger2WebMvc
@ConditionalOnWebApplication
@Import(BeanValidatorPluginsConfiguration.class)
public class Knife4jConfig {
    @Bean
    Docket createRestApi(){
        return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo())
                .enable(true)
                .select()
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                .paths(PathSelectors.any())  // 包下的类，生成接口文档
                .build()
                .securitySchemes(security());
    }

    private ApiInfo apiInfo(){
        return new ApiInfoBuilder().title("数据接口API").description("接口文档")
                .termsOfServiceUrl("http://***.com/")
                .version("1.0.0")
                .build();
    }

    private List<ApiKey> security(){
        return Collections.singletonList(new ApiKey("token", "token", "header"));
    }
}
