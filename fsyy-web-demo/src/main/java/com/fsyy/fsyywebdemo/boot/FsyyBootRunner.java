package com.fsyy.fsyywebdemo.boot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class FsyyBootRunner implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(FsyyBootRunner.class);

    @Override
    public void run(String... args) throws Exception {
        if(args.length == 1){
            if("kill".equalsIgnoreCase(args[0])){
                // System.exit(1);
                throw new Exception("kill with signal!");
            }
        }
        LOGGER.info("this is fsyy's web demo!");
    }
}
