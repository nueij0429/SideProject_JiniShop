package com.jinishop.jinishop.global.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    // 모든 예외 응답 포맷 통일

    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}