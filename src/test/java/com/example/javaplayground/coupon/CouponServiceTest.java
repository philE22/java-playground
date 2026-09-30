package com.example.javaplayground.coupon;

import com.example.javaplayground.coupon.domain.Coupon;
import com.example.javaplayground.coupon.domain.CouponIssueRepository;
import com.example.javaplayground.coupon.domain.CouponRepository;
import com.example.javaplayground.coupon.exception.CouponException;
import com.example.javaplayground.coupon.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
@SpringBootTest
@DisplayName("CouponService - 쿠폰 발급 로직 기능 테스트")
class CouponServiceTest {

    @Autowired
    CouponService couponService;

    @Autowired
    CouponRepository couponRepository;
    @Autowired
    private CouponIssueRepository couponIssueRepository;

    Long setUp(int stock) {
        var coupon = Coupon.create("선착순 쿠폰", stock,
                LocalDateTime.now().minusMonths(1), LocalDateTime.now().plusMonths(1));

        couponRepository.save(coupon);
        return coupon.getId();
    }

    @Test
    void 사용자는_쿠폰ID를_지정해_발급받을_수_있다() {
        // given
        Long couponId = setUp(10);

        // when
        var result = couponService.issue(couponId, 1L);

        // then
        assertThat(result.couponId()).isEqualTo(couponId);
        assertThat(result.userId()).isEqualTo(1L);
        assertThat(couponService.getStock(couponId).totalQuantity()).isEqualTo(10);
        assertThat(couponService.getStock(couponId).issuedQuantity()).isEqualTo(1);
    }

    @Test
    void 한명당_하나의_쿠폰만_발급받을_수_있다() {
        // given
        Long couponId = setUp(10);
        couponService.issue(couponId, 1L);

        // when then
        assertThatThrownBy(() -> couponService.issue(couponId, 1L))
                .isInstanceOf(CouponException.class)
                .hasMessage(ErrorCode.ALREADY_ISSUED.name());
    }

    @Test
    void 정해진_수량만큼만_발급할_수_있다() {
        // given
        Long couponId = setUp(2);

        // when
        couponService.issue(couponId, 1L);
        couponService.issue(couponId, 2L);

        // then
        assertThatThrownBy(() -> couponService.issue(couponId, 3L))
                .isInstanceOf(CouponException.class)
                .hasMessage(ErrorCode.COUPON_SOLD_OUT.name());
    }

    @Timeout(10)
    @Test
    void 동시에_요청해도_정해진_수량만큼만_발급한다() {
        // given
        int stock = 3;
        int requestCount = 5;
        Long couponId = setUp(stock);

        var startLatch = new CountDownLatch(1);
        var successCount = new AtomicInteger();
        var failures = new ConcurrentLinkedQueue<Throwable>();

        // when
        try (ExecutorService es = Executors.newFixedThreadPool(requestCount)) {
            for (int i = 1; i <= requestCount; i++) {
                long userId = i;

                es.submit(() -> {
                    try {
                        startLatch.await();
                        couponService.issue(couponId, userId);
                        successCount.incrementAndGet();
                    } catch (Throwable t) {
                        failures.add(t);
                    }
                });

            }

            startLatch.countDown();
        }

        // then
        assertThat(successCount.get()).isEqualTo(stock);
        assertThat(failures).hasSize(2)
                .allSatisfy(t -> assertThat(t)
                        .isInstanceOf(CouponException.class)
                        .hasMessage(ErrorCode.COUPON_SOLD_OUT.name())
                );
        assertThat(couponService.getStock(couponId).issuedQuantity()).isEqualTo(stock);
        assertThat(couponIssueRepository.findByCoupon_Id(couponId).size()).isEqualTo(stock);
    }
}