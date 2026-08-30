package com.example.javaplayground.coupon.v1;

import com.example.javaplayground.coupon.CouponIssuedResponse;
import com.example.javaplayground.coupon.CouponService;
import com.example.javaplayground.coupon.CouponStockResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Redis 없이 MySQL 만을 사용한 발급 로직을 구현한다.
 */
@Service
@RequiredArgsConstructor
public class CouponServiceV1 implements CouponService {

    @Override
    public CouponIssuedResponse issue(Long couponId, Long userId) {
        return null;
    }

    @Override
    public CouponIssuedResponse findCoupon(Long couponId, Long userId) {
        return null;
    }

    @Override
    public CouponStockResponse getStock(Long couponId) {
        return null;
    }
}
