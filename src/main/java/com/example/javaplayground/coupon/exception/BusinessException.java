package com.example.javaplayground.coupon.exception;

public class BusinessException extends RuntimeException {

    private ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
    super(errorCode.name());
}}
