package com.fsyy.fsyywebdemo.web.exception;

import com.fsyy.fsyywebdemo.web.mvc.AjaxResult;
import com.fsyy.fsyywebdemo.web.mvc.BaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerExceptionHandler extends BaseController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ControllerExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public AjaxResult handlerException(Exception e){
        LOGGER.error(e.getMessage(), e);
        // return ajaxResult(Status.ERROR, "系统内部异常", e.getMessage());
        return error("系统内部异常", e.getMessage());
    }
}
