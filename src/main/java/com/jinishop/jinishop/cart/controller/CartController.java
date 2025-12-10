package com.jinishop.jinishop.cart.controller;

import com.jinishop.jinishop.cart.domain.CartItem;
import com.jinishop.jinishop.cart.dto.AddCartItemRequest;
import com.jinishop.jinishop.cart.dto.CartItemResponse;
import com.jinishop.jinishop.cart.dto.UpdateCartItemQuantityRequest;
import com.jinishop.jinishop.cart.service.CartService;
import com.jinishop.jinishop.global.response.ResponseDto;
import com.jinishop.jinishop.product.dto.ProductResponse;
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
    public ResponseDto<List<CartItemResponse>> getCartItems(@RequestParam Long userId) {
        List<CartItem> items = cartService.getCartItems(userId);
        List<CartItemResponse> result = items.stream()
                .map(CartItemResponse::new)
                .toList();
        return ResponseDto.ok(result);
    }

    // 장바구니에 상품 추가
    @PostMapping("/items")
    public ResponseDto<Void> addCartItem(@RequestBody AddCartItemRequest request) {
        cartService.addItem(request.getUserId(), request.getProductOptionId(), request.getQuantity());
        return ResponseDto.ok(null);
    }

    // 장바구니 아이템 수량 변경
    @PatchMapping("/items/{cartItemId}")
    public ResponseDto<Void> updateCartItemQuantity(
            @PathVariable Long cartItemId,
            @RequestBody UpdateCartItemQuantityRequest request
    ) {
        cartService.updateItemQuantity(cartItemId, request.getQuantity());
        return ResponseDto.ok(null);
    }

    // 장바구니 아이템 삭제
    @DeleteMapping("/items/{cartItemId}")
    public ResponseDto<Void> deleteCartItem(@PathVariable Long cartItemId) {
        cartService.removeItem(cartItemId);
        return ResponseDto.ok(null);
    }
}