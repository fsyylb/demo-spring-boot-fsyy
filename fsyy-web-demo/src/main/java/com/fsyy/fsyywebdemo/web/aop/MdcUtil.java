package com.fsyy.fsyywebdemo.web.aop;

import org.slf4j.MDC;

import java.util.UUID;

public class MdcUtil {
    private MdcUtil(){}

    private static final String TRACEID = "traceId";

    private static final String SPANID = "spanId";

    public static String getTraceId(){
        return MDC.get(TRACEID);
    }

    public static String getSpanId() {
        return MDC.get(SPANID);
    }

    public static void setTraceId(){
        MDC.put(TRACEID, uuid());
    }

    public static void setSpanId(){
        MDC.put(SPANID, uuid());
    }

    public static void clear(){
        MDC.clear();
    }

    public static String uuid(){
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}
