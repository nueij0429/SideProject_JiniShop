package com.jinishop.jinishop.order.service;

import com.jinishop.jinishop.global.exception.BusinessException;
import com.jinishop.jinishop.global.exception.ErrorCode;
import com.jinishop.jinishop.order.domain.Order;
import com.jinishop.jinishop.order.domain.OrderStatus;
import com.jinishop.jinishop.order.dto.OrderItemResponse;
import com.jinishop.jinishop.order.dto.OrderResponse;
import com.jinishop.jinishop.order.event.OrderStatusChangedEvent;
import com.jinishop.jinishop.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderAdminService {

    private final OrderRepository orderRepository;
    private final ApplicationEventPublisher publisher;

    @Transactional
    public OrderResponse changeStatus(Long orderId, OrderStatus next) {
        // N+1 해결 조회 메서드 사용
        Order order = orderRepository.findWithItemsById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        order.setStatus(next);

        // 이벤트 발행 (웹소켓 푸시 리스너가 받게)
        publisher.publishEvent(new OrderStatusChangedEvent(
                order.getId(),
                order.getUser().getId(),
                order.getUser().getEmail(),
                next
        ));

        List<OrderItemResponse> items = order.getOrderItems().stream()
                .map(OrderItemResponse::new)
                .toList();

        return new OrderResponse(order, items);
    }
}