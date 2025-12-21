package com.jinishop.jinishop.admin.controller;

import com.jinishop.jinishop.global.exception.BusinessException;
import com.jinishop.jinishop.global.exception.ErrorCode;
import com.jinishop.jinishop.global.response.ResponseDto;
import com.jinishop.jinishop.order.domain.OrderStatus;
import com.jinishop.jinishop.order.dto.AdminOrderStatusUpdateRequest;
import com.jinishop.jinishop.order.dto.OrderResponse;
import com.jinishop.jinishop.order.service.OrderAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/admin/orders")
public class AdminOrderController {

    private final OrderAdminService orderAdminService;

    // 관리자용 - 주문 상태 변경
    @PatchMapping("/{orderId}/status")
    public ResponseDto<OrderResponse> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestBody AdminOrderStatusUpdateRequest request
    ) {
        OrderStatus next = parseOrderStatus(request.getStatus());
        OrderResponse updated = orderAdminService.changeStatus(orderId, next);
        return ResponseDto.ok(updated);
    }

    private OrderStatus parseOrderStatus(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_REQUIRED);
        }
        try {
            return OrderStatus.valueOf(raw);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_INVALID);
        }
    }
}
