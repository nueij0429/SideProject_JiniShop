package com.jinishop.jinishop.cart.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AddCartItemRequest {
    private Long userId;
    private Long productOptionId;
    private int quantity;
}