package com.jinishop.jinishop.order.service;

import com.jinishop.jinishop.global.exception.BusinessException;
import com.jinishop.jinishop.global.exception.ErrorCode;
import com.jinishop.jinishop.order.domain.Order;
import com.jinishop.jinishop.order.domain.OrderStatus;
import com.jinishop.jinishop.order.event.OrderStatusChangedEvent;
import com.jinishop.jinishop.order.repository.OrderRepository;
import com.jinishop.jinishop.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderStatusFacade {
    // 상태 변경을 한 곳으로 모으는 Facade

    private final OrderRepository orderRepository;
    private final ApplicationEventPublisher publisher;

    @Transactional
    public void changeStatus(Long orderId, OrderStatus next) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        order.setStatus(next);

        User user = order.getUser();
        publisher.publishEvent(new OrderStatusChangedEvent(
                order.getId(),
                user.getId(),
                user.getEmail(),   // JWT subject로 쓰는 값
                next
        ));
    }
}