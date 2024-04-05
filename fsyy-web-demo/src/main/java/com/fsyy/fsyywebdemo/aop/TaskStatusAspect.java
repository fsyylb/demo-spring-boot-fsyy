package com.fsyy.fsyywebdemo.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Slf4j
// @Aspect
// @Component
public class TaskStatusAspect {
    @Autowired
    JdbcTemplate jdbcTemplate;

    /**
     * 切入点，方便被引用通知引用
     * com.fsyy.fsyywebdemo.service.asyn包及子包下包含注解Async的类
     */
    @Pointcut("within(com.fsyy.fsyywebdemo.service.asyn..*) && @@annotation(org.springframework.scheduling.annotation.Async)")
    public void pointcut(){

    }

    @Before("pointcut()")
    public void doBefore(JoinPoint joinPoint){
        updateStatus("任务运行中", getTaskId(joinPoint));
    }

    @AfterReturning("pointcut()")
    public void doAfterReturing(JoinPoint joinPoint){
        updateStatus("任务运行成功", getTaskId(joinPoint));
    }

    @AfterThrowing("pointcut()")
    public void doAfterThrowing(JoinPoint joinPoint){
        updateStatus("任务运行失败，请稍后再试", getTaskId(joinPoint));
    }

    /**
     * 更新任务状态值
     * @param status
     * @param taskId
     */
    private void updateStatus(String status, String taskId){
        log.info("status : {}, taskId : {}", status, taskId);
        /*int count = jdbcTemplate.update("update ****");
        if(count == 0){
            jdbcTemplate.update("insert into *******");
        }*/
    }

    /**
     * 获取taskId,注意taskId一定要是最后一个参数
     * @param joinPoint
     * @return
     */
    private String getTaskId(JoinPoint joinPoint){
        // 获取的是最后一个参数的值
        Object[] args = joinPoint.getArgs();
        return String.valueOf(args[args.length - 1]);
    }
}
