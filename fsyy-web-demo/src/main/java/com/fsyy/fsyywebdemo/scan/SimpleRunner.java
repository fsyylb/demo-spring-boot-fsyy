package com.fsyy.fsyywebdemo.scan;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SimpleRunner implements IRunner{
    @Override
    public void run() {
        doBefore();
        log.info("SimpleRunner running!");
        doAfter();
    }

    private void doBefore(){
        log.info("Before……");
    }

    private void doAfter(){
        log.info("After……");
    }
}
