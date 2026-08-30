package com.example.javaplayground.coupon;

import java.time.LocalDateTime;

public record CouponIssuedResponse(
        Long issuedId,
        Long couponId,
        Long userId,
        LocalDateTime issuedAt
) {
}
