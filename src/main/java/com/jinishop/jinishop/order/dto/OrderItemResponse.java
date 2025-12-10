package com.jinishop.jinishop.order.dto;

import com.jinishop.jinishop.order.domain.OrderItem;
import lombok.Getter;

@Getter
public class OrderItemResponse {
    // 주문 상품 응답 DTO

    private final Long orderItemId;
    private final Long productOptionId;
    private final String productName;
    private final String color;
    private final String size;
    private final Long orderPrice;
    private final int quantity;

    public OrderItemResponse(OrderItem orderItem) {
        this.orderItemId = orderItem.getId();
        this.productOptionId = orderItem.getProductOption().getId();
        this.productName = orderItem.getProductOption().getProduct().getName();
        this.color = orderItem.getProductOption().getColor();
        this.size = orderItem.getProductOption().getSize();
        this.orderPrice = orderItem.getOrderPrice();
        this.quantity = orderItem.getQuantity();
    }
}