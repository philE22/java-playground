package com.example.javaplayground.coupon.v1;

import com.example.javaplayground.coupon.CouponIssuedResponse;
import com.example.javaplayground.coupon.CouponService;
import com.example.javaplayground.coupon.CouponStockResponse;
import com.example.javaplayground.coupon.domain.Coupon;
import com.example.javaplayground.coupon.domain.CouponIssue;
import com.example.javaplayground.coupon.domain.CouponIssueRepository;
import com.example.javaplayground.coupon.domain.CouponRepository;
import com.example.javaplayground.coupon.exception.BusinessException;
import com.example.javaplayground.coupon.exception.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Redis 없이 MySQL 만을 사용한 발급 로직을 구현한다.
 */
@Transactional
@Service
@RequiredArgsConstructor
public class CouponServiceV1 implements CouponService {

    private final CouponRepository couponRepository;
    private final CouponIssueRepository couponIssueRepository;

    @Override
    public synchronized CouponIssuedResponse issue(Long couponId, Long userId) {
        Coupon coupon = getCoupon(couponId);

        long issuedCount = getIssuedCount(coupon);

        if (coupon.getStock() <= issuedCount)
            throw new BusinessException(ErrorCode.COUPON_SOLD_OUT);

        if (couponIssueRepository.findByCouponAndUserId(coupon, userId).isPresent())
            throw new BusinessException(ErrorCode.ALREADY_ISSUED);

        CouponIssue couponIssue = CouponIssue.create(coupon, userId);
        couponIssueRepository.save(couponIssue);
        return new CouponIssuedResponse(
                couponIssue.getId(),
                couponId,
                userId,
                couponIssue.getIssuedAt()
        );
    }

    @Override
    public CouponIssuedResponse findCoupon(Long couponId, Long userId) {
        return null;
    }

    @Override
    public CouponStockResponse getStock(Long couponId) {
        Coupon coupon = getCoupon(couponId);

        int issuedCount = (int) getIssuedCount(coupon);

        return new CouponStockResponse(couponId, coupon.getStock(), issuedCount);
    }

    private Coupon getCoupon(Long couponId) {
        return couponRepository.findById(couponId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COUPON_NOT_FOUND));
    }

    private long getIssuedCount(Coupon coupon) {
        return couponIssueRepository.countByCoupon(coupon);
    }
}
