package com.fsyy.fsyywebdemo.web.mvc;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @GetMapping("/")
    public String hello(){
        throw new RuntimeException("hello world!");
        // return "hello world!";
    }
}
