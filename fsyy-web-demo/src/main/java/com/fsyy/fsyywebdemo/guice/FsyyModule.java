package com.fsyy.fsyywebdemo.guice;

import com.fsyy.fsyywebdemo.scan.EchoRunner;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class FsyyModule extends AbstractModule {
    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new FsyyModule());
        Class<EchoRunner> clazz = EchoRunner.class;
        EchoRunner runner = injector.getInstance(clazz);
        runner.run();
    }
}
