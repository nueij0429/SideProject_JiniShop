package com.jinishop.jinishop.product.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductUpdateRequest {
    // 상품 수정 요청 DTO

    private String name;
    private String description;
    private Long price;
    private String status;  // "ON_SALE", "STOP_SALE"
}