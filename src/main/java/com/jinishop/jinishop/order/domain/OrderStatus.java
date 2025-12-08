package com.jinishop.jinishop.order.domain;

public enum OrderStatus {
    CREATED, // 주문 생성만 된 상태
    PAID, // 결제가 완료된 상태
    CANCELED // 주문 취소된 상태
}