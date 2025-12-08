package com.jinishop.jinishop.product.dto;

import com.jinishop.jinishop.product.domain.Product;
import lombok.Getter;

@Getter
public class ProductResponse {

    // 상품 응답 DTO
    private final Long id;
    private final String name;
    private final String description;
    private final Long price;
    private final String status;

    public ProductResponse(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.price = product.getPrice().longValue();
        this.status = product.getStatus().name();
    }
}