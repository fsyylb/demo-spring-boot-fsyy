package com.fsyy.fsyywebdemo.web.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Order(-5)
public class FsyyControllerAop extends FsyyCommon{
    @Pointcut("execution(* com.fsyy.fsyywebdemo..*Controller.*(..))")
    public void controllerLog(){

    }

    @Before("controllerLog()")
    public void doBefore(JoinPoint point){
        String className = point.getTarget().getClass().getName();
        String methodName = point.getTarget().getClass().getName() + "." + point.getSignature().getName();
        this.before(className, methodName, point.getArgs());
    }

    // 2024-03-22 09:41:39.157  INFO 1316 --- [nio-8080-exec-1] com.fsyy.fsyywebdemo.web.aop.FsyyCommon  : traceId : ffd21be97f044d839c6089a8ecd04140 ; spanId : f512d94940eb4a099a33178c4cbf9b74 ; className : com.fsyy.fsyywebdemo.web.test.TestController ; methodName : com.fsyy.fsyywebdemo.web.test.TestController.info ; output : fsyy : a man ; timeCosted : 2 ; returnCode : 200
    @AfterReturning(pointcut = "controllerLog()", returning = "ret")
    public void doAfterReturning(Object ret){
        this.after(ret);
    }
}
