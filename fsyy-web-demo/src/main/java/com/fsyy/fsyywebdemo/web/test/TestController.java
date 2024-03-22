package com.fsyy.fsyywebdemo.web.test;

import com.fsyy.fsyywebdemo.web.test.pojo.TestRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class TestController {
    @GetMapping("/")
    public String hello(){
        throw new RuntimeException("hello world!");
        // return "hello world!";
    }

    @PostMapping("/show/info")
    public String info(@Valid @RequestBody TestRequest request){
        return request.getName() + " : " + request.getInfo();
    }
}
