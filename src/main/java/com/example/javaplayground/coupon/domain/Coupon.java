package com.example.javaplayground.coupon.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String couponName;

    private int stock;

    private LocalDateTime startedAt;

    private LocalDateTime endedAt;

    public static Coupon create(String couponName, int stock, LocalDateTime startedAt, LocalDateTime endedAt) {
        var coupon = new Coupon();
        coupon.couponName = couponName;
        coupon.stock = stock;
        coupon.startedAt = startedAt;
        coupon.endedAt = endedAt;
        return coupon;
    }
}
