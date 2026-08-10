package com.example.javaplayground.redis.views;

public record PostDetailResponse(
        String title,
        String content,
        Long viewCount
) {
}
