package com.fsyy.fsyywebdemo.scan;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EchoRunner implements IRunner{
    @Override
    public void run() {
        log.info("EchoRunner echo : Hello World!");
    }
}
