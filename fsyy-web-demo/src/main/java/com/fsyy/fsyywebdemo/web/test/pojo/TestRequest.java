package com.fsyy.fsyywebdemo.web.test.pojo;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

public class TestRequest {
    @NotBlank(message = "姓名必填")
    @Length(max = 8, message = "姓名过长")
    private String name;

    @NotBlank(message = "info不能为空")
    private String info;

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "TestRequest{" +
                "name='" + name + '\'' +
                ", info='" + info + '\'' +
                '}';
    }
}
