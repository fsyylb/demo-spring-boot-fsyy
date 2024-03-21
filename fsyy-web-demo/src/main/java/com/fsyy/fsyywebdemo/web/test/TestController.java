package com.fsyy.fsyywebdemo.web.test;

import com.fsyy.fsyywebdemo.web.test.pojo.TestRequest;
import org.springframework.web.bind.annotation.*;

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
