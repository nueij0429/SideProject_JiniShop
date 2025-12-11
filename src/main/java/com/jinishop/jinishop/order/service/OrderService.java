package com.jinishop.jinishop.order.service;

import com.jinishop.jinishop.cart.domain.Cart;
import com.jinishop.jinishop.cart.domain.CartItem;
import com.jinishop.jinishop.cart.repository.CartItemRepository;
import com.jinishop.jinishop.cart.service.CartService;
import com.jinishop.jinishop.global.exception.BusinessException;
import com.jinishop.jinishop.global.exception.ErrorCode;
import com.jinishop.jinishop.order.domain.Order;
import com.jinishop.jinishop.order.domain.OrderItem;
import com.jinishop.jinishop.order.domain.OrderStatus;
import com.jinishop.jinishop.order.repository.OrderItemRepository;
import com.jinishop.jinishop.order.repository.OrderRepository;
import com.jinishop.jinishop.product.domain.ProductOption;
import com.jinishop.jinishop.stock.service.StockService;
import com.jinishop.jinishop.user.domain.User;
import com.jinishop.jinishop.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final UserService userService;
    private final CartService cartService;
    private final StockService stockService;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartItemRepository cartItemRepository;

    // 회원의 장바구니 기준으로 주문 생성 + 재고 차감
    @Transactional
    public Long placeOrder(Long userId) {
        // 회원 & 장바구니 조회
        User user = userService.getUser(userId);
        Cart cart = cartService.getCart(userId);

        List<CartItem> cartItems = cartItemRepository.findByCart(cart);
        if (cartItems.isEmpty()) {
            throw new BusinessException(ErrorCode.CART_EMPTY); // 장바구니가 비어있을 시
        }

        // 총 금액 계산
        long totalAmount = cartItems.stream()
                .mapToLong(item -> {
                    long price = item.getProductOption().getProduct().getPrice();
                    return price * item.getQuantity();
                })
                .sum();

        // 주문 생성
        Order order = Order.builder()
                .user(user)
                .totalAmount(totalAmount)
                .status(OrderStatus.CREATED)
                .build();

        orderRepository.save(order);

        // 주문 아이템 생성 + 재고 차감
        for (CartItem cartItem : cartItems) {
            ProductOption option = cartItem.getProductOption();

            // 재고 차감 (비관적 락 버전)
            stockService.decreaseStockWithPessimisticLock(option.getId(), cartItem.getQuantity());

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .productOption(option)
                    .orderPrice(option.getProduct().getPrice())
                    .quantity(cartItem.getQuantity())
                    .build();

            orderItemRepository.save(orderItem);
        }

        // 주문 완료 후 장바구니 비우기
        cartItemRepository.deleteAll(cartItems);

        return order.getId();
    }

    // 회원의 모든 주문 목록 조회 - N+1 해결
    @Transactional(readOnly = true)
    public List<Order> getOrders(Long userId) {
        User user = userService.getUser(userId);
        //return orderRepository.findByUser(user);
        return orderRepository.findWithItemsByUser(user);
    }

    // 단일 주문 상세 조회 - N+1 해결
    @Transactional(readOnly = true)
    public Order getOrder(Long orderId) {
        //return orderRepository.findById(orderId)
        return orderRepository.findWithItemsById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND)); // 주문 조회 실패
    }
}