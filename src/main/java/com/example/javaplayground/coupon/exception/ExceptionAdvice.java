package com.example.javaplayground.coupon.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(CouponException.class)
    public ResponseEntity<?> couponException(CouponException e) {
        var errorCode = e.getErrorCode();

        return ResponseEntity.status(errorCode.getStatusCode()).body(errorCode.name());
    }
}
