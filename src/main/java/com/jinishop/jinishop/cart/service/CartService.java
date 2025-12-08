package com.jinishop.jinishop.cart.service;

import com.jinishop.jinishop.cart.domain.Cart;
import com.jinishop.jinishop.cart.domain.CartItem;
import com.jinishop.jinishop.cart.repository.CartItemRepository;
import com.jinishop.jinishop.cart.repository.CartRepository;
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

    // 회원의 장바구니 조회
    public Cart getCart(Long userId) {
        User user = userService.getUser(userId);
        return cartRepository.findByUser(user).orElse(null);
    }

    // 회원의 장바구니 조회 또는 생성
    @Transactional
    public Cart getOrCreateCart(Long userId) {
        User user = userService.getUser(userId);
        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart cart = Cart.builder()
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
                .orElseThrow(() -> new NoSuchElementException("상품 옵션을 찾을 수 없음. id=" + productOptionId));

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
                .orElseThrow(() -> new NoSuchElementException("장바구니 아이템을 찾을 수 없음. id=" + cartItemId));

        // dirty checking (상태 변경 검사)
        // 수량이 0 이하이면 삭제
        if (quantity <= 0) {
            cartItemRepository.delete(item);
        } else {
            item.setQuantity(quantity);
        }
    }

    // 상품 삭제
    @Transactional
    public void removeItem(Long cartItemId) {
        cartItemRepository.deleteById(cartItemId);
    }

    public List<CartItem> getCartItems(Long userId) {
        Cart cart = getCart(userId);
        if (cart == null) return List.of();
        return cartItemRepository.findByCart(cart);
    }
}