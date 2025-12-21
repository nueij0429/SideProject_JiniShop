package com.jinishop.jinishop.order.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AdminOrderStatusUpdateRequest {
    // 관리자 전용 주문 상태 변경 DTO

    private String status; // ex) "PAYMENT_SUCCESS"
}