package com.example.javaplayground.coupon.exception;

import lombok.Getter;

public class CouponException extends RuntimeException {

    @Getter
    private final ErrorCode errorCode;

    public CouponException(ErrorCode errorCode) {
        super(errorCode.name());
        this.errorCode = errorCode;
    }
}
