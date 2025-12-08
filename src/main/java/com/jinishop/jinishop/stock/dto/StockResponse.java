package com.jinishop.jinishop.stock.dto;

import com.jinishop.jinishop.stock.domain.Stock;
import lombok.Getter;

@Getter
public class StockResponse {
    // 재고 DTO

    private final Long productOptionId;
    private final int quantity;

    public StockResponse(Stock stock) {
        this.productOptionId = stock.getProductOption().getId();
        this.quantity = stock.getQuantity();
    }
}
