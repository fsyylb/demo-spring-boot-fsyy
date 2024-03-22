package com.fsyy.fsyywebdemo.web.aop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class FsyyCommon {
    private static final Logger LOGGER = LoggerFactory.getLogger(FsyyCommon.class);

    private static ThreadLocal<LogParameter> record = new ThreadLocal<>();
    private static ThreadLocal<Map<String, String>> methodLocal = new ThreadLocal<>();

    private final static String CLASS_NAME = "className";
    private final static String METHOD_NAME = "methodName";
    private final static String SUC_RETURN_CODE = "200";

    /**
     *  开始进入，初始化参数
     * @param className
     * @param methodName
     * @param args
     */
    public void before(String className, String methodName, Object[] args){
        Map<String, String> map = new HashMap<>();
        map.put(CLASS_NAME, className);
        map.put(METHOD_NAME, methodName);
        methodLocal.set(map);
        MdcUtil.setTraceId();
        MdcUtil.setSpanId();
        LogParameter logParameter = generateLogParameter(args);
        record.set(logParameter);
    }

    /**
     * 正常返回打印log
     * @param object
     */
    public void after(Object object){
        try {
            String output = object == null ? "" : object.toString();
            LogParameter logParameter = record.get();
            LOGGER.info("traceId : " + MdcUtil.getTraceId() + " ; spanId : " + MdcUtil.getSpanId()
                    + " ; className : " + methodLocal.get().get(CLASS_NAME)
                    + " ; methodName : " + methodLocal.get().get(METHOD_NAME)
                    + " ; output : " + output
                    + " ; timeCosted : " + (System.currentTimeMillis() - logParameter.getStartTime())
                    + " ; returnCode : " + SUC_RETURN_CODE
            );
        }finally {
            record.remove();
            methodLocal.remove();
            MdcUtil.clear();
        }
    }

    public LogParameter generateLogParameter(Object[] args){
        StringBuilder sb = new StringBuilder();
        for(Object obj : args){
            sb.append(obj).append("_");
        }
        if(sb.length() > 0){
            sb.deleteCharAt(sb.length() - 1);
        }
        return new LogParameter(sb.toString(), System.currentTimeMillis());
    }

}
