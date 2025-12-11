package com.jinishop.jinishop.order.controller;

import com.jinishop.jinishop.global.response.ResponseDto;
import com.jinishop.jinishop.order.domain.Order;
import com.jinishop.jinishop.order.domain.OrderItem;
import com.jinishop.jinishop.order.dto.OrderItemResponse;
import com.jinishop.jinishop.order.dto.OrderResponse;
import com.jinishop.jinishop.order.dto.PlaceOrderRequest;
import com.jinishop.jinishop.order.repository.OrderItemRepository;
import com.jinishop.jinishop.order.service.OrderService;
import com.jinishop.jinishop.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseDto<OrderResponse> placeOrder(@AuthenticationPrincipal CustomUserDetails user) {
        Long orderId = orderService.placeOrder(user.getId());

        Order order = orderService.getOrder(orderId); // 주문 + 아이템까지 한 번에 가져옴

        //List<OrderItem> orderItems = orderItemRepository.findByOrder(order);

        List<OrderItemResponse> itemResponses = order.getOrderItems().stream()
                .map(OrderItemResponse::new)
                .toList();

        return ResponseDto.ok(new OrderResponse(order, itemResponses));
    }

    // 내 주문 목록 조회
    @GetMapping
    public ResponseDto<List<OrderResponse>> getOrders(@AuthenticationPrincipal CustomUserDetails user) {
        Long userId = user.getId();
        List<Order> orders = orderService.getOrders(userId);

        List<OrderResponse> result = orders.stream()
                .map(order -> {
                    //List<OrderItem> orderItems = orderItemRepository.findByOrder(order);
                    List<OrderItemResponse> itemResponses = order.getOrderItems().stream()
                            .map(OrderItemResponse::new)
                            .toList();
                    return new OrderResponse(order, itemResponses);
                })
                .toList();
        return ResponseDto.ok(result);
    }

    // 주문 상세 조회
    @GetMapping("/{orderId}")
    public ResponseDto<OrderResponse> getOrder(@PathVariable Long orderId, @AuthenticationPrincipal CustomUserDetails user) {
        Order order = orderService.getOrder(orderId); // 주문 + 아이템까지 한 번에 가져옴

        //List<OrderItem> orderItems = orderItemRepository.findByOrder(order);

        List<OrderItemResponse> itemResponses = order.getOrderItems().stream()
                .map(OrderItemResponse::new)
                .toList();

        return ResponseDto.ok(new OrderResponse(order, itemResponses));
    }
}