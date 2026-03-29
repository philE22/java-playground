package com.example.javaplayground.redis.session;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RequestMapping("/auth")
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final SessionService sessionService;

    @PostMapping("/login/{userId}")
    public ResponseEntity<UUID> login(@PathVariable Long userId) {
        UUID loginId = sessionService.login(userId);

        return ResponseEntity.ok(loginId);
    }

    // 로그인 이후 특정 유저 요청
    @GetMapping("/request")
    public ResponseEntity<SessionDto> request(@RequestHeader("X-Session-Id") String sessionId) {
        // 인증 과정 - 인터셉터에서 처리
        var sessionOptional = sessionService.findSession(sessionId);

        if (sessionOptional.isEmpty()) {
            log.error("로그인이 필요합니다");
            return ResponseEntity.notFound().build();
        }

        var session = sessionService.refresh(sessionId);    // sliding session

        // 비지니스 로직
        // ...


        return  ResponseEntity.ok(session);
    }
}
