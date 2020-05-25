package com.fnordfisch.i3connect;

import com.fnordfisch.i3connect.service.DataService;
import com.fnordfisch.i3connect.service.TokenService;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@EnableCaching
@ImportResource({"classpath*:services.xml"})
public class Config {
    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(TokenService.CACHE_NAME, DataService.CACHE_NAME);
    }

}
