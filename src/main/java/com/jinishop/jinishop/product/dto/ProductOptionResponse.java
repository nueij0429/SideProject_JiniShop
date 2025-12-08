package com.jinishop.jinishop.product.dto;

import com.jinishop.jinishop.product.domain.ProductOption;
import lombok.Getter;

@Getter
public class ProductOptionResponse {
    // 상품 옵션 DTO

    private final Long id;
    private final String color;
    private final String size;

    public ProductOptionResponse(ProductOption option) {
        this.id = option.getId();
        this.color = option.getColor();
        this.size = option.getSize();
    }
}
