package com.fsyy.banner.config;

import com.fsyy.banner.core.BannerApplicationRunner;
import org.springframework.context.annotation.Bean;

// @Configuration
public class FsyyBannerAutoConfiguration {
    @Bean
    public BannerApplicationRunner bannerApplicationRunner(){
        return new BannerApplicationRunner();
    }
}
