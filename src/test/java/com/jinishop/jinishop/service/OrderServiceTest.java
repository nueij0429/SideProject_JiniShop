package com.jinishop.jinishop.service;

import com.jinishop.jinishop.cart.domain.Cart;
import com.jinishop.jinishop.cart.domain.CartItem;
import com.jinishop.jinishop.cart.repository.CartItemRepository;
import com.jinishop.jinishop.cart.repository.CartRepository;
import com.jinishop.jinishop.cart.service.CartService;
import com.jinishop.jinishop.global.exception.BusinessException;
import com.jinishop.jinishop.global.exception.ErrorCode;
import com.jinishop.jinishop.order.domain.Order;
import com.jinishop.jinishop.order.domain.OrderItem;
import com.jinishop.jinishop.order.domain.OrderStatus;
import com.jinishop.jinishop.order.repository.OrderItemRepository;
import com.jinishop.jinishop.order.repository.OrderRepository;
import com.jinishop.jinishop.order.service.OrderService;
import com.jinishop.jinishop.product.domain.Product;
import com.jinishop.jinishop.product.domain.ProductOption;
import com.jinishop.jinishop.product.domain.ProductStatus;
import com.jinishop.jinishop.product.repository.ProductOptionRepository;
import com.jinishop.jinishop.product.repository.ProductRepository;
import com.jinishop.jinishop.stock.domain.Stock;
import com.jinishop.jinishop.stock.repository.StockRepository;
import com.jinishop.jinishop.user.domain.User;
import com.jinishop.jinishop.user.domain.UserRole;
import com.jinishop.jinishop.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional  // 각 테스트 끝나고 롤백 → DB 깨끗하게 유지
class OrderServiceTest {

    @Autowired
    OrderService orderService;
    @Autowired
    CartService cartService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    ProductOptionRepository productOptionRepository;
    @Autowired
    StockRepository stockRepository;
    @Autowired
    CartRepository cartRepository;
    @Autowired
    CartItemRepository cartItemRepository;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    OrderItemRepository orderItemRepository;

    Long userId;
    Long productOptionId;

    // 테스트마다 실행되는 공통 준비 단계
    @BeforeEach
    void setUp() {
        // 1. 유저 생성
        User user = User.builder()
                .email("test@test.com")
                .password("password")
                .name("테스트유저")
                .role(UserRole.USER)
                .build();
        userRepository.save(user);
        userId = user.getId();

        // 2. 상품 + 옵션 + 재고 생성
        Product product = Product.builder()
                .name("테스트 상품")
                .description("테스트용 상품입니다.")
                .price(10000L)
                .status(ProductStatus.ON_SALE)
                .build();
        productRepository.save(product);

        ProductOption option = ProductOption.builder()
                .product(product)
                .color("Black")
                .size("M")
                .build();
        productOptionRepository.save(option);
        productOptionId = option.getId();

        Stock stock = Stock.builder()
                .productOption(option)
                .quantity(100)  // 재고 100개
                .version(0)
                .build();
        stockRepository.save(stock);

        // 3. 장바구니에 해당 옵션 2개 담기
        cartService.addItem(userId, productOptionId, 2);
    }

    @Test
    @DisplayName("장바구니 기반 주문 생성 시: 주문/주문상품 생성, 재고 차감, 장바구니 비움")
    void placeOrder_success_flow() {
        // when
        Long orderId = orderService.placeOrder(userId);

        // then
        Order order = orderService.getOrder(orderId);
        List<OrderItem> orderItems = orderItemRepository.findByOrder(order);

        // 1. 주문 기본 정보 검증
        assertThat(order.getUser().getId()).isEqualTo(userId);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CREATED);

        // 2. 주문 아이템이 1개 생성되었는지
        assertThat(orderItems).hasSize(1);
        OrderItem orderItem = orderItems.get(0);

        // 3. 주문 아이템 정보 검증
        assertThat(orderItem.getProductOption().getId()).isEqualTo(productOptionId);
        assertThat(orderItem.getQuantity()).isEqualTo(2);
        assertThat(orderItem.getOrderPrice()).isEqualTo(10000L); // 상품 가격
        assertThat(order.getTotalAmount()).isEqualTo(10000L * 2); // 총 금액 = 20000

        // 4. 재고가 100 → 98로 차감되었는지 확인
        ProductOption option = productOptionRepository.findById(productOptionId).orElseThrow();
        Stock stock = stockRepository.findByProductOption(option).orElseThrow();
        assertThat(stock.getQuantity()).isEqualTo(98);

        // 5. 장바구니가 비워졌는지 확인
        Cart cart = cartRepository.findByUser(order.getUser()).orElseThrow();
        List<CartItem> cartItems = cartItemRepository.findByCart(cart);
        assertThat(cartItems).isEmpty();
    }

    // 재고 부족 테스트
    @Test
    @DisplayName("재고가 부족하면 주문 생성 시 STOCK_NOT_ENOUGH 예외 발생")
    void placeOrder_fail_when_stock_not_enough() {
        // given
        // 현재 옵션/재고/장바구니 상태는 @BeforeEach에서 세팅된 상태:
        // - stock: 100
        // - cart: 해당 옵션 2개 담겨 있음

        // 재고를 5개로 줄이고,
        ProductOption option = productOptionRepository.findById(productOptionId).orElseThrow();
        Stock stock = stockRepository.findByProductOption(option).orElseThrow();
        stock.setQuantity(1); // 재고를 1개로 줄여놓고 (주문 수량 2개보다 적게)

        // 장바구니에 수량 2개 그대로 둔 상태에서 주문 시도
        // when & then
        org.assertj.core.api.Assertions.assertThatThrownBy(() -> orderService.placeOrder(userId))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.STOCK_NOT_ENOUGH);
    }
}