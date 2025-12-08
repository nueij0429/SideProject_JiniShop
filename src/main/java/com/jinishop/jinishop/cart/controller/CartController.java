package com.jinishop.jinishop.cart.controller;

import com.jinishop.jinishop.cart.domain.CartItem;
import com.jinishop.jinishop.cart.dto.AddCartItemRequest;
import com.jinishop.jinishop.cart.dto.CartItemResponse;
import com.jinishop.jinishop.cart.dto.UpdateCartItemQuantityRequest;
import com.jinishop.jinishop.cart.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    // 장바구니 조회 (나중에 Security 붙이고 인증 정보에서 가져오는 형태로 변경할 예정)
    @GetMapping
    public List<CartItemResponse> getCartItems(@RequestParam Long userId) {
        List<CartItem> items = cartService.getCartItems(userId);
        return items.stream()
                .map(CartItemResponse::new)
                .toList();
    }

    // 장바구니에 상품 추가
    @PostMapping("/items")
    public void addCartItem(@RequestBody AddCartItemRequest request) {
        cartService.addItem(request.getUserId(), request.getProductOptionId(), request.getQuantity());
    }

    // 장바구니 아이템 수량 변경
    @PatchMapping("/items/{cartItemId}")
    public void updateCartItemQuantity(
            @PathVariable Long cartItemId,
            @RequestBody UpdateCartItemQuantityRequest request
    ) {
        cartService.updateItemQuantity(cartItemId, request.getQuantity());
    }

    // 장바구니 아이템 삭제
    @DeleteMapping("/items/{cartItemId}")
    public void deleteCartItem(@PathVariable Long cartItemId) {
        cartService.removeItem(cartItemId);
    }
}