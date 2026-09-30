package com.example.javaplayground.coupon.domain;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CouponIssueRepository extends JpaRepository<CouponIssue, Long> {
    long countByCoupon(Coupon coupon);

    Optional<CouponIssue> findByCouponAndUserId(Coupon coupon, long userId);

    List<CouponIssue> findByCoupon_Id(Long couponId);
}
