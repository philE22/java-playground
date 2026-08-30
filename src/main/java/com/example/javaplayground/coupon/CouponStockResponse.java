package com.example.javaplayground.coupon;

public record CouponStockResponse(
        Long couponId,
        int totalQuantity,
        int issuedQuantity
) {
}
