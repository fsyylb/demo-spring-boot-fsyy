package com.fsyy.ssetouch.core.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.servlet.ServletContext;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Spring Context 工具类
 */

@Slf4j
@Component
public class SpringContextUtils implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    private static String serverBaseUrl = null;

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        log.info("###### execute setApplicationContext ######");
        SpringContextUtils.applicationContext = context;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static <T> T getBean(Class<T> clazz){
        Assert.notNull(applicationContext, "applicationContext is null");
        return applicationContext.getBean(clazz);
    }

    /**
     * execute @PostConstruct May be SpringContextUtils not inited, throw NullPointerException
     *
     * @return
     */
    public static String getActiveProfile(){
        Assert.notNull(applicationContext, "applicationContext is null");
        String[] profiles = applicationContext.getEnvironment().getActiveProfiles();
        return StringUtils.join(profiles, ",");
    }

    /**
     * can use in @PostConstruct
     *
     * @param context
     * @return
     */
    public static String getActiveProfile(ApplicationContext context){
        Assert.notNull(context, "context is null");
        String[] profiles = context.getEnvironment().getActiveProfiles();
        return StringUtils.join(profiles, ",");
    }


    /**
     * get web服务基准地址，一般为 http://${ip}:${port}/${contentPath}
     *
     * @return
     * @throws UnknownHostException
     * @see [类、类#方法、类#成员]
     */
    public static String getServerBaseURL() throws UnknownHostException {
        ServletContext servletContext = getBean(ServletContext.class);
        Assert.notNull(servletContext, "servletContext is null");
        if(serverBaseUrl == null){
            String ip = InetAddress.getLocalHost().getHostAddress();
            serverBaseUrl = "http://" + ip + ":" + getProperty("server.port") + servletContext.getContextPath();
        }
        return serverBaseUrl;
    }

    /**
     * getProperty
     *
     * @param key eg:server.port
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static String getProperty(String key){
        return applicationContext.getEnvironment().getProperty(key, "");
    }
}
