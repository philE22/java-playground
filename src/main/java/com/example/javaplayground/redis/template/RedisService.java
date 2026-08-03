package com.example.javaplayground.redis.template;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;

    public RedisService(
//            @Qualifier("simpleRedisTemplate") RedisTemplate<String, String> stringRedisTemplate
            StringRedisTemplate stringRedisTemplate // 이게 위 simpleRedisTemplate 빈과 동일하게 동작함
    ) {
        this.redisTemplate = stringRedisTemplate;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        log.info("시작");
        setData();
        log.info("종료");
    }

    public void setData() {
        redisTemplate.opsForValue()
                .set("setData2", "value2");
        log.info("데이터 set 완료");
    }

}
