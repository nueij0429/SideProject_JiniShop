package com.jinishop.jinishop.cart.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AddCartItemRequest {
    // 장바구니의 주인 찾는 것은 항상 토큰에서 결정
    private Long productOptionId;
    private int quantity;
}