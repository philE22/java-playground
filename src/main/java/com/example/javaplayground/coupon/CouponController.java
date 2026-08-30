package com.example.javaplayground.coupon;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController("/api/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{couponId}/issue")
    public CouponIssuedResponse issue(@PathVariable Long couponId,
                                      @RequestHeader("X-USER-ID") Long userId) {

        return couponService.issue(couponId, userId);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{couponId}/issue/me")
    public CouponIssuedResponse getIssued(@PathVariable Long couponId,
                                          @RequestHeader("X-USER-ID") Long userId){

        return couponService.findCoupon(couponId, userId);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{couponId}/stock")
    public CouponStockResponse getStock(@PathVariable Long couponId){

        return couponService.getStock(couponId);
    }
}
