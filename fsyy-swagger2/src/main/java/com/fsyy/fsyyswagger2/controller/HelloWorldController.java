package com.fsyy.fsyyswagger2.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "Hello World!")
@RestController("/v1/demo")
public class HelloWorldController {
    @ApiOperation(value = "hello world示例")
    @GetMapping("/hello")
    public String hello(){
        return "Hello World!";
    }
}
