package com.jinishop.jinishop.order.domain;

public enum OrderStatus {
    CREATED, // 주문 생성만 된 상태
    PAYMENT_PENDING, // 결제 대기
    PAYMENT_SUCCESS, // 결제 성공
    PAYMENT_FAILED, // 결제 실패
    CANCELED // 주문 취소된 상태
}