package com.example.javaplayground.redis.session.config;

import com.example.javaplayground.redis.session.SessionDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisSessionConfig {

    @Bean
    public RedisTemplate<String, SessionDto> sessionRedisTemplate(RedisConnectionFactory connectionFactory) {
        //TODO 여기 설정 파악. 간결화 할 수는 없는건지
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        RedisTemplate<String, SessionDto> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(objectMapper,  SessionDto.class));
        return template;
    }
}
