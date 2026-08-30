package com.example.javaplayground.coupon.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ErrorCode {
    COUPON_SOLD_OUT(409),
    ALREADY_ISSUED(409),
    NOT_IN_EVENT_PERIOD(400),
    COUPON_NOT_FOUND(404),
    ISSUE_NOT_FOUND(404),
    ;

    private int statusCode;
}
