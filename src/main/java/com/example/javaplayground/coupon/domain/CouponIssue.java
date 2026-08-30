package com.example.javaplayground.coupon.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CouponIssue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Coupon coupon;

    private long userId;

    private LocalDateTime issuedAt;

    public static CouponIssue create(Coupon coupon, long userId) {
        var couponIssue = new CouponIssue();
        couponIssue.coupon = coupon;
        couponIssue.userId = userId;
        couponIssue.issuedAt = LocalDateTime.now();
        return couponIssue;
    }
}
