package com.fsyy.ssetouch.touch.service;

import com.fsyy.ssetouch.core.utils.JsonBeanUtils;
import com.fsyy.ssetouch.core.utils.SpringContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * Server-Sent Events <BR>
 * https://blog.csdn.net/hhl18730252820/article/details/126244274
 */
@Slf4j
public class SSEServer {
    private static List<SseEmitter> sseEmitters = new CopyOnWriteArrayList<>();

    private SSEServer(){
        super();
    }

    public static SseEmitter connect(){
        SseEmitter sseEmitter = new SseEmitter(0L); // 设置超时时间，0表示不过期，默认是30秒，超时时间未完成会抛出异常

        // 注册回调
        sseEmitter.onCompletion(completionCallBack(sseEmitter));
        sseEmitter.onError(errorCallBack(sseEmitter));
        sseEmitter.onTimeout(timeOutCallBack(sseEmitter));
        sseEmitters.add(sseEmitter);
        log.info("##### create new sse connect, count: {}", sseEmitters.size());

        // 推送近10分钟流量数据
        TrafficService trafficeService = SpringContextUtils.getBean(TrafficService.class);
        List<Map<String, Object>> cards = trafficeService.queryCards();
        if(!cards.isEmpty()){
            Date now = new Date();
            Date before = DateUtils.addMinutes(now, -10);
            for (Map<String, Object> card : cards){
                List<Map<String, Object>> list = trafficeService.query(String.valueOf(card.get("card")), before, now);
                list.stream().forEach(message -> {
                    try{
                        String json = JsonBeanUtils.beanToJson(Collections.singletonList(message), false);
                        sseEmitter.send(json);
                    }catch (IOException e){
                        log.error(e.getMessage(), e.getCause());
                    }
                });
            }
        }
        return sseEmitter;
    }


    public static void batchSendMessage(Object message){
        sseEmitters.forEach(it -> {
            try{
                it.send(message, MediaType.APPLICATION_JSON);
            }catch (IOException e){
                log.error(e.getMessage(), e.getCause());
                remove(it);
            }
        });
    }

    /**
     * 指定name，发送message
     *
     * @param name
     * @param message
     */
    public static void batchSendMessage(String name, String message){
        sseEmitters.forEach(it -> {
            try{
                it.send(SseEmitter.event().name(name).data(message));
            }catch (IOException e){
                log.error(e.getMessage(), e.getCause());
                remove(it);
            }
        });
    }

    public static void remove(SseEmitter s){
        if(sseEmitters.contains(s)){
            sseEmitters.remove(s);
            log.info("###### remove SseEmitter, count: {}", sseEmitters.size());
        }
    }

    private static Runnable completionCallBack(SseEmitter s){
        return () -> {
            log.info("结束连接");
            remove(s);
        };
    }

    private static Runnable timeOutCallBack(SseEmitter s){
        return () -> {
            log.info("连接超时");
            remove(s);
        };
    }

    private static Consumer<Throwable> errorCallBack(SseEmitter s){
        return throwable -> {
            log.error("连接异常");
            remove(s);
        };
    }
}
