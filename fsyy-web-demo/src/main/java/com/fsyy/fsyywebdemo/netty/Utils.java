package com.fsyy.fsyywebdemo.netty;

import java.util.Date;

public class Utils {
    public static void println(String msg){
        System.out.println("[Thread] : " + Thread.currentThread().getName() +
                " --- " + msg + " --- " + new Date());
    }
}
