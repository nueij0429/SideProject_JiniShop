package com.jinishop.jinishop.stock.domain;

public enum StockHistoryReason {
    ORDER, // 주문 때문에 줄었는지
    MANUAL_ADJUST // 관리자가 수동으로 수정했는지
}