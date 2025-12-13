package com.jinishop.jinishop.messaging.kafka.dto;

public enum EventType {
    // 주문/결제 라이프사이클을 명시적으로 표현하는 이벤트 타입 Enum

    ORDER_CREATED,
    PAYMENT_REQUESTED,
    PAYMENT_SUCCEEDED,
    PAYMENT_FAILED
}