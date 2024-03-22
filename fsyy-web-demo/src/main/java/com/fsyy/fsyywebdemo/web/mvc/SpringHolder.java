package com.fsyy.fsyywebdemo.web.mvc;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SpringHolder implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        setStaticApplicationContext(applicationContext);
    }

    private static void setStaticApplicationContext(ApplicationContext applicationContext) {
        SpringHolder.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static Environment getEnv(){
        return applicationContext.getEnvironment();
    }

    public static <T> T getBean(Class<T> clazz){
        return applicationContext.getBean(clazz);
    }

    public static Object getBean(String name){
        return applicationContext.getBean(name);
    }

    public static <T> T getBean(String name, Class<T> clazz){
        return applicationContext.getBean(name, clazz);
    }

    public static <T> Map<String, T> getBeansOfType(Class<T> clazz){
        return applicationContext.getBeansOfType(clazz);
    }

    public static <T> T getProperty(String key, Class<T> clazz){
        return getEnv().getProperty(key, clazz);
    }

    public static <T> T getProperty(String key, Class<T> clazz, T defaultValue){
        return getEnv().getProperty(key, clazz, defaultValue);
    }

    public static String getProperty(String key){
        return getEnv().getProperty(key);
    }

    public static String getProperty(String key, String defaultValue){
        return getEnv().getProperty(key, defaultValue);
    }
}
