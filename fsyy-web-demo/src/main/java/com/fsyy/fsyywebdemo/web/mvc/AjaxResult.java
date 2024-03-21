package com.fsyy.fsyywebdemo.web.mvc;

import com.fsyy.fsyywebdemo.web.mvc.BaseController.Status;

public class AjaxResult {
    private String status;  // 状态码 SUCCESS 成功; ERROR 异常; NODATA 无数据;

    private String msg; // 状态消息

    private Object result; // 结果集

    public AjaxResult() {
    }

    public AjaxResult(Status status){
        this.status = status.getValue();
    }

    public AjaxResult(Status status, String msg){
        this.status = status.getValue();
        this.msg = msg;
    }

    public AjaxResult(Status status, Object result){
        this.status = status.getValue();
        this.result = result;
    }

    public AjaxResult(Status status, String msg, Object result){
        this.status = status.getValue();
        this.msg = msg;
        this.result = result;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
