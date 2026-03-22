package com.example.javaplayground.redis.cache;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class CacheService {

    @Cacheable(cacheNames = "caffeineCache", key = "#cacheId")
    public CacheDto getCache(String cacheId) {
        slowDbCall();

        return new CacheDto(
                cacheId,
                "상품-" + cacheId,
                Instant.now().toString()
        );
    }

    @CacheEvict(cacheNames = "caffeineCache", key = "#cacheId")
    public void evictCache(String cacheId) {
        // 상품 수정/삭제 후 캐시 무효화할 때 사용
    }

    private void slowDbCall() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
