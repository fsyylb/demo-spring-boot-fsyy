package com.fsyy.fsyywebdemo.scan;

import org.reflections.Reflections;

import java.util.Set;

public class RunnerReflection {
    public static void scanRunner() throws IllegalAccessException, InstantiationException {
        String scanPackage = "com.fsyy.fsyywebdemo";
        Reflections reflections = new Reflections(scanPackage);
        Set<Class<? extends IRunner>> subClasses = reflections.getSubTypesOf(IRunner.class);
        for(Class<? extends IRunner> subClass : subClasses){
            subClass.newInstance().run();
        }
    }

    public static void main(String[] args) throws InstantiationException, IllegalAccessException {
        scanRunner();
    }
}
