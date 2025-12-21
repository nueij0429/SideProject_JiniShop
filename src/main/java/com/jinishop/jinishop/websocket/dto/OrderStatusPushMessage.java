package com.jinishop.jinishop.websocket.dto;

import java.time.LocalDateTime;

public record OrderStatusPushMessage(
        // 푸시로 내려줄 배송 상태 메시지 DTO

        Long orderId,
        Long userId,
        String status,
        String text,
        LocalDateTime occurredAt
) {
    public static OrderStatusPushMessage of(Long orderId, Long userId, String status, String text) {
        return new OrderStatusPushMessage(orderId, userId, status, text, LocalDateTime.now());
    }
}