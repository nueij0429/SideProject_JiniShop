package com.jinishop.jinishop.order.controller;

import com.jinishop.jinishop.order.domain.Order;
import com.jinishop.jinishop.order.domain.OrderItem;
import com.jinishop.jinishop.order.dto.OrderItemResponse;
import com.jinishop.jinishop.order.dto.OrderResponse;
import com.jinishop.jinishop.order.dto.PlaceOrderRequest;
import com.jinishop.jinishop.order.repository.OrderItemRepository;
import com.jinishop.jinishop.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final OrderItemRepository orderItemRepository;

    // 주문 생성
    @PostMapping
    public OrderResponse placeOrder(@RequestBody PlaceOrderRequest request) {
        Long orderId = orderService.placeOrder(request.getUserId());
        Order order = orderService.getOrder(orderId);
        List<OrderItem> orderItems = orderItemRepository.findByOrder(order);

        List<OrderItemResponse> itemResponses = orderItems.stream()
                .map(OrderItemResponse::new)
                .toList();

        return new OrderResponse(order, itemResponses);
    }

    // 내 주문 목록 조회
    @GetMapping
    public List<OrderResponse> getOrders(@RequestParam Long userId) {
        List<Order> orders = orderService.getOrders(userId);

        return orders.stream()
                .map(order -> {
                    List<OrderItem> orderItems = orderItemRepository.findByOrder(order);
                    List<OrderItemResponse> itemResponses = orderItems.stream()
                            .map(OrderItemResponse::new)
                            .toList();
                    return new OrderResponse(order, itemResponses);
                })
                .toList();
    }

    // 주문 상세 조회
    @GetMapping("/{orderId}")
    public OrderResponse getOrder(@PathVariable Long orderId) {
        Order order = orderService.getOrder(orderId);
        List<OrderItem> orderItems = orderItemRepository.findByOrder(order);

        List<OrderItemResponse> itemResponses = orderItems.stream()
                .map(OrderItemResponse::new)
                .toList();

        return new OrderResponse(order, itemResponses);
    }
}