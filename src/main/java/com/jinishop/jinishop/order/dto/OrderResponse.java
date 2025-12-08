package com.jinishop.jinishop.order.dto;

import com.jinishop.jinishop.order.domain.Order;
import lombok.Getter;

import java.util.List;

@Getter
public class OrderResponse {
    // 전체 주문 응답 DTO

    private final Long orderId;
    private final Long userId;
    private final Long totalAmount;
    private final String status;
    private final List<OrderItemResponse> items;

    public OrderResponse(Order order, List<OrderItemResponse> items) {
        this.orderId = order.getId();
        this.userId = order.getUser().getId();
        this.totalAmount = order.getTotalAmount().longValue();
        this.status = order.getStatus().name();
        this.items = items;
    }
}