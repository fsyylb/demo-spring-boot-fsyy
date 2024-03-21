package com.fsyy.fsyywebdemo.web.mvc;

public class BaseController {
    public AjaxResult ajaxResult(Status status){
        return new AjaxResult(status);
    }

    public AjaxResult ajaxResult(Status status, String msg){
        return new AjaxResult(status, msg);
    }

    public AjaxResult ajaxResult(Status status, Object result){
        return new AjaxResult(status, result);
    }

    public AjaxResult ajaxResult(Status status, String msg, Object result){
        return new AjaxResult(status, msg, result);
    }

    public AjaxResult success(String msg){
        return new AjaxResult(Status.SUCCESS, msg);
    }

    public AjaxResult error(String msg){
        return new AjaxResult(Status.ERROR, msg);
    }

    public AjaxResult success(String msg, Object result){
        return new AjaxResult(Status.SUCCESS, msg, result);
    }

    public AjaxResult error(String msg, Object result){
        return new AjaxResult(Status.ERROR, msg, result);
    }

    public AjaxResult success(Object result){
        return new AjaxResult(Status.SUCCESS, result);
    }

    public AjaxResult error(Object result){
        return new AjaxResult(Status.ERROR, result);
    }

    public enum Status {
        SUCCESS("success"),
        ERROR("error"),
        NODATA("nodata");

        private String value;

        Status(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
