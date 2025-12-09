package com.jinishop.jinishop.stock.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StockAdjustRequest {
    // 재고 수동 조정 요청 DTO

    private int changeAmount;
}