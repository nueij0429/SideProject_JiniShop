package com.jinishop.jinishop.cart.dto;

import com.jinishop.jinishop.cart.domain.CartItem;
import lombok.Getter;

@Getter
public class CartItemResponse {
    // 장바구니 DTO

    private final Long cartItemId;
    private final Long productOptionId;
    private final String productName;
    private final String color;
    private final String size;
    private final Long price;
    private final int quantity;

    public CartItemResponse(CartItem item) {
        this.cartItemId = item.getId();
        this.productOptionId = item.getProductOption().getId();
        this.productName = item.getProductOption().getProduct().getName();
        this.color = item.getProductOption().getColor();
        this.size = item.getProductOption().getSize();
        this.price = item.getProductOption().getProduct().getPrice();
        this.quantity = item.getQuantity();
    }
}