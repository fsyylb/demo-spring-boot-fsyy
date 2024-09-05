package com.fsyy.ssetouch;

import com.fsyy.ssetouch.core.utils.SpringContextUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SsetouchApplication {

    public static void main(String[] args) {
        SpringApplication.run(SsetouchApplication.class, args);
    }

    @Bean
    CommandLineRunner init(){
        return args -> {
            if(SystemUtils.IS_OS_WINDOWS)  // 防止非windows系统报错，启动失败
            {
                String url = SpringContextUtils.getServerBaseURL();
                if(StringUtils.containsNone(url, "-")) // junit port:-1
                {
                    Runtime.getRuntime().exec("cmd /c start /min " + url + "/sse/connect/000");
                    Runtime.getRuntime().exec("cmd /c start /min " + url + "/doc.html");
                }
            }
        };
    }
}
