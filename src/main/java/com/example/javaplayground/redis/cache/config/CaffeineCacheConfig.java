package com.example.javaplayground.redis.cache.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

//@Configuration
//@EnableCaching
public class CaffeineCacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager manager = new CaffeineCacheManager("caffeineCache");
        manager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(10)
//                .expireAfterAccess(10, TimeUnit.SECONDS)
                .expireAfterWrite(5, TimeUnit.MINUTES)     // 데이터 정합성이 중요한 경우 사용 (상품, 설정 등)
                .recordStats()
        );

        return manager;
    }
}
