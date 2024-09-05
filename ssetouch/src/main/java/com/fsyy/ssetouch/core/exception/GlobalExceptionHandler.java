package com.fsyy.ssetouch.core.exception;

import com.fsyy.ssetouch.core.JsonResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public JsonResult<Object> handleBadRequest(Exception exception){
        // JSR303参数校验异常
        if(exception instanceof BindException){
            BindingResult bindingResult = ((BindException)exception).getBindingResult();
            if(null != bindingResult && bindingResult.hasErrors()){
                List<String> errMsg = new ArrayList<>();
                bindingResult.getFieldErrors().forEach(fieldError -> errMsg.add(fieldError.getDefaultMessage()));
                Collections.sort(errMsg);
                return JsonResult.error(StringUtils.join(errMsg, ","));
            }
        }

        if(exception instanceof MethodArgumentNotValidException){
            BindingResult bindingResult = ((MethodArgumentNotValidException) exception).getBindingResult();
            if(null != bindingResult && bindingResult.hasErrors()){
                // stream写法优化
                return JsonResult.error(bindingResult.getFieldErrors().stream().map(e -> e.getDefaultMessage()).sorted().collect(Collectors.joining(",")));
            }
        }

        // 其余情况
        log.error("Error: handlerBadRequest StackTrace : {}", exception);
        return JsonResult.error(StringUtils.defaultString(exception.getMessage(), "系统异常，请联系管理员"));
    }
}
