package com.example.javapractice.redis.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/caches")
@RequiredArgsConstructor
public class CacheController {

    private final CacheService cacheService;

    @GetMapping("/{id}")
    public CacheDto get(@PathVariable String id) {
        return cacheService.getCache(id);
    }

    @DeleteMapping("/{id}/cache")
    public void evict(@PathVariable("id") String id) {
        cacheService.evictCache(id);
    }
}
