package com.fsyy.fsyywebdemo.web.exception;

import com.fsyy.fsyywebdemo.web.mvc.AjaxResult;
import com.fsyy.fsyywebdemo.web.mvc.BaseController;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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



    // @Valid注解，异常获取
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public AjaxResult handlerMethodArgumentNotValidException(MethodArgumentNotValidException e){
        LOGGER.error(e.getMessage(), e);
        BindingResult bindResult = e.getBindingResult();
        List<String> errMsgs = new ArrayList<>();
        List<FieldError> fieldErrors = bindResult.getFieldErrors();
        for(FieldError fieldError : fieldErrors){
            errMsgs.add(fieldError.getDefaultMessage());
        }
        Collections.sort(errMsgs);
        return error("系统内部异常", StringUtils.join(errMsgs, ",")); // commons-lang3
    }
}
