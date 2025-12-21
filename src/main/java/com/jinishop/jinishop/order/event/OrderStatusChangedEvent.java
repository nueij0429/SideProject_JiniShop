package com.jinishop.jinishop.order.event;

import com.jinishop.jinishop.order.domain.OrderStatus;

public record OrderStatusChangedEvent(
        // 주문 상태 변경 이벤트 발행/수신 구조

        Long orderId,
        Long userId,
        String userEmail,
        OrderStatus status
) {}
