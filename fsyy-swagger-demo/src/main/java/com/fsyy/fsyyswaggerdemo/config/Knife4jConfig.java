package com.fsyy.fsyyswaggerdemo.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import io.swagger.annotations.Api;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
@EnableOpenApi     //开启 Swagger3 ，可不写
@EnableKnife4j     //开启 knife4j ，可不写
public class Knife4jConfig {
    @Bean
    public Docket createRestApi() {
        // Swagger 3 使用的是：DocumentationType.OAS_30
        return new Docket(DocumentationType.OAS_30)
                // 定义是否开启swagger，false为关闭，可以通过变量控制
                .enable(true)
                // 将api的元信息设置为包含在json ResourceListing响应中。
                .apiInfo(new ApiInfoBuilder()
                        .title("Knife4j接口文档")
                        // 描述
                        .description("平台服务管理api")
                        .contact(new Contact("fsyy", "http://localhost:8080/", "fsyy@qq.com"))
                                .version("1.0.0")
                                .build())
                                // 分组名称
                                .groupName("FSYY-SWAGGER")
                                // 选择哪些接口作为swagger的doc发布
                                .select()
                                // 要扫描的API(Controller)基础包
                                // .apis(RequestHandlerSelectors.basePackage("com.example"))
                                //                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                                .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
                                .paths(PathSelectors.any())
                                .build();
    }
}

/*

//改配置文件和Swagger配置文件一致，只是添加了两个注解
package com.example.swagger.config;

        import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
        import org.springframework.context.annotation.Bean;
        import org.springframework.context.annotation.Configuration;
        import springfox.documentation.builders.ApiInfoBuilder;
        import springfox.documentation.builders.PathSelectors;
        import springfox.documentation.builders.RequestHandlerSelectors;
        import springfox.documentation.service.ApiInfo;
        import springfox.documentation.service.Contact;
        import springfox.documentation.spi.DocumentationType;
        import springfox.documentation.spring.web.plugins.Docket;

@Configuration
//@EnableSwagger2	   //开启 Swagger2
@EnableOpenApi     //开启 Swagger3 ，可不写
@EnableKnife4j     //开启 knife4j ，可不写
public class Knife4jConfig {
    @Bean
    public Docket createRestApi() {
        // Swagger 2 使用的是：DocumentationType.SWAGGER_2
        // Swagger 3 使用的是：DocumentationType.OAS_30
        return new Docket(DocumentationType.OAS_30)
                // 定义是否开启swagger，false为关闭，可以通过变量控制
                .enable(true)
                // 将api的元信息设置为包含在json ResourceListing响应中。
                .apiInfo(new ApiInfoBuilder()
                        .title("Knife4j接口文档")
                        // 描述
                        .description("平台服务管理api")
                        .contact(new Contact("作者", "地址", "邮箱或联系方式))
                                .version("1.0.0")
                                .build())
                                // 分组名称
                                .groupName("1.0")
                                // 选择哪些接口作为swagger的doc发布
                                .select()
                                // 要扫描的API(Controller)基础包
                                .apis(RequestHandlerSelectors.basePackage("com.example"))
                                //                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                                .paths(PathSelectors.any())
                                .build();
    }
}



 */