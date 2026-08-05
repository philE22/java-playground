package com.example.javaplayground.redis.session;

import java.time.LocalDateTime;
import java.util.List;

public record SessionDto(
        Long userId,
        String userName,
        List<Role> roles,
        LocalDateTime loginAt
) {
}
