package com.fsyy.fsyywebdemo.monitor.event;

import com.fsyy.fsyywebdemo.web.mvc.SpringHolder;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.ProtocolHandler;
import org.apache.coyote.http11.Http11NioProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class EventScheduledTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventScheduledTask.class);
    private boolean tomcatStarted = false;

    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Scheduled(fixedRate = 60 * 1000)
    public void threadPoolEvent(){
        int corePoolSize = threadPoolTaskExecutor.getCorePoolSize();
        int maxPoolSize = threadPoolTaskExecutor.getMaxPoolSize();
        int queueSize = threadPoolTaskExecutor.getThreadPoolExecutor().getQueue().size();
        int activeCount = threadPoolTaskExecutor.getActiveCount();

        LOGGER.info("当前线程池信息：");
        LOGGER.info("   核心线程数：" + corePoolSize);
        LOGGER.info("   最大线程数：" + maxPoolSize);
        LOGGER.info("   任务队列大小：" + queueSize);
        LOGGER.info("   活跃线程数：" + activeCount);
    }

    // @Scheduled(initialDelay = 10 * 1000, fixedRate = 60 * 1000)
    @Scheduled(fixedRate = 60 * 1000)
    public void httpLinkEvent() throws LifecycleException {
        ApplicationContext applicationContext = SpringHolder.getApplicationContext();
        if(!(applicationContext instanceof ServletWebServerApplicationContext)){
            LOGGER.info("非ServletWebServerApplicationContext！");
            return;
        }

        WebServer webServer = ((ServletWebServerApplicationContext) applicationContext).getWebServer();
        if(!(webServer instanceof TomcatWebServer)){
            LOGGER.info("非TomcatWebServer！");
            return;
        }

        if(!tomcatStarted){
            // 等待SpringBoot embedded tomcat started
            long startTime = System.currentTimeMillis();
            while(!Util.getTomcatWebServerStarted((TomcatWebServer) webServer)) {
                ThreadHints.onSpinWait();
            }
            long endTime = System.currentTimeMillis();
            LOGGER.info("wating embedded tomcat started costed time is : " + (endTime - startTime) + "ms");
            tomcatStarted = true;
        }



        Connector connector = ((TomcatWebServer) webServer).getTomcat().getConnector();
        ProtocolHandler protocolHandler = connector.getProtocolHandler();

        if(!(protocolHandler instanceof Http11NioProtocol)){
            LOGGER.info("非Http11NioProtocol！");
            return;
        }
        long connectionCount = ((Http11NioProtocol) protocolHandler).getConnectionCount();
        long maxConnections = ((Http11NioProtocol) protocolHandler).getMaxConnections();
        LOGGER.info("当前连接信息：");
        LOGGER.info("   当前连接数：" + connectionCount);
        LOGGER.info("   最大连接数：" + maxConnections);
    }
}
