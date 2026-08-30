package com.example.javaplayground.coupon;

public interface CouponService {

    CouponIssuedResponse issue(Long couponId, Long userId);

    CouponIssuedResponse findCoupon(Long couponId, Long userId);

    CouponStockResponse getStock(Long couponId);
}
