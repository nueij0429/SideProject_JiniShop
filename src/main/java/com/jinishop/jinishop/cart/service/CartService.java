package com.jinishop.jinishop.cart.service;

import com.jinishop.jinishop.cart.domain.Cart;
import com.jinishop.jinishop.cart.domain.CartItem;
import com.jinishop.jinishop.cart.repository.CartItemRepository;
import com.jinishop.jinishop.cart.repository.CartRepository;
import com.jinishop.jinishop.global.exception.BusinessException;
import com.jinishop.jinishop.global.exception.ErrorCode;
import com.jinishop.jinishop.product.domain.ProductOption;
import com.jinishop.jinishop.product.repository.ProductOptionRepository;
import com.jinishop.jinishop.user.domain.User;
import com.jinishop.jinishop.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductOptionRepository productOptionRepository;
    private final UserService userService;

    // 회원의 장바구니 조회 (엔티티 자체 조회용)
    public Cart getCart(Long userId) {
        User user = userService.getUser(userId);
        return cartRepository.findByUser(user)
                .orElseThrow(() -> new BusinessException(ErrorCode.CART_NOT_FOUND)); // 장바구니 조회 실패 시
    }

    // 회원의 장바구니 조회 또는 생성
    @Transactional
    public Cart getOrCreateCart(Long userId) {
        User user = userService.getUser(userId);
        return cartRepository.findByUser(user)
                .orElseGet(() -> {  // lazy 생성 패턴
                    Cart cart = Cart.builder() // 장바구니 없으면 생성
                            .user(user)
                            .build();
                    return cartRepository.save(cart);
                });
    }

    // 장바구니에 상품 추가
    @Transactional
    public void addItem(Long userId, Long productOptionId, int quantity) {
        Cart cart = getOrCreateCart(userId);

        ProductOption option = productOptionRepository.findById(productOptionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_OPTION_NOT_FOUND)); // 상품 옵션을 찾을 수 없을 시

        // 이미 같은 옵션이 담겨 있으면 수량만 증가
        List<CartItem> items = cartItemRepository.findByCart(cart);
        CartItem existing = items.stream()
                .filter(ci -> ci.getProductOption().getId().equals(productOptionId))
                .findFirst()
                .orElse(null);

        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + quantity);
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .productOption(option)
                    .quantity(quantity)
                    .build();
            cartItemRepository.save(newItem);
        }
    }

    // 상품 수량 변경
    @Transactional
    public void updateItemQuantity(Long cartItemId, int quantity) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CART_ITEM_NOT_FOUND)); // 장바구니 상품을 찾을 수 없을 때

        // dirty checking (상태 변경 검사)
        // 수량이 0 이하이면 삭제
        if (quantity <= 0) {
            cartItemRepository.delete(item);
        } else {
            item.setQuantity(quantity);
        }
    }

    // 장바구니 상품 삭제
    @Transactional
    public void removeItem(Long cartItemId) {
        cartItemRepository.deleteById(cartItemId);
    }

    // 장바구니에 담긴 모든 상품 반환 (조회 API용) - N+1 해결 버전
    public List<CartItem> getCartItems(Long userId) {
        Cart cart = getCart(userId);
        return cartItemRepository.findByCart(cart);
    }
}