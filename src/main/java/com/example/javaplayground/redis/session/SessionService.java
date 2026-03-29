package com.example.javaplayground.redis.session;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SessionService {

    public static final String SESSION_PREFIX = "session:";
    private final RedisTemplate<String, SessionDto> redisTemplate;

    public UUID login(Long userId) {
        // 로그인 검증 로직
        // ...

        var session = new SessionDto(
                userId,
                "홍길동" + userId,
                List.of(Role.ADMIN),
                LocalDateTime.now()
        );

        UUID uuid = UUID.randomUUID();
        redisTemplate.opsForValue()
                .set(SESSION_PREFIX + uuid, session, Duration.ofSeconds(30));

        return uuid;
    }

    public SessionDto refresh(String sessionId) {
        SessionDto session = redisTemplate.opsForValue()
                .getAndExpire(SESSION_PREFIX + sessionId, Duration.ofSeconds(30));

        return session;
    }

    public Optional<SessionDto> findSession(String uuid) {
        var session = redisTemplate.opsForValue().get(SESSION_PREFIX + uuid);

        return Optional.ofNullable(session);
    }
}
