package com.jinishop.jinishop.product.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductCreateRequest {
    // 상품 등록 요청 DTO

    private String name;
    private String description;
    private Long price;
    private String status;  // "ON_SALE", "STOP_SALE"
}