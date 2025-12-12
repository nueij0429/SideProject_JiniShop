package com.jinishop.jinishop.cart.controller;

import com.jinishop.jinishop.cart.domain.CartItem;
import com.jinishop.jinishop.cart.dto.AddCartItemRequest;
import com.jinishop.jinishop.cart.dto.CartItemResponse;
import com.jinishop.jinishop.cart.dto.UpdateCartItemQuantityRequest;
import com.jinishop.jinishop.cart.service.CartService;
import com.jinishop.jinishop.global.response.ResponseDto;
import com.jinishop.jinishop.product.dto.ProductResponse;
import com.jinishop.jinishop.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SecurityRequirement(name = "bearerAuth") // 이 컨트롤러의 모든 엔드포인트는 JWT 필요하다고 Swagger에 표시
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    // 장바구니 조회
    @GetMapping
    public ResponseDto<List<CartItemResponse>> getCartItems(@AuthenticationPrincipal CustomUserDetails user) {
        Long userId = user.getId();
        List<CartItem> items = cartService.getCartItems(userId);
        
        List<CartItemResponse> result = items.stream()
                .map(CartItemResponse::new)
                .toList();
        return ResponseDto.ok(result);
    }

    // 장바구니에 상품 추가
    @PostMapping("/items")
    public ResponseDto<Void> addCartItem(@AuthenticationPrincipal CustomUserDetails user, @RequestBody AddCartItemRequest request) {
        cartService.addItem(user.getId(), request.getProductOptionId(), request.getQuantity());
        return ResponseDto.ok(null);
    }

    // 장바구니 아이템 수량 변경
    @PatchMapping("/items/{cartItemId}")
    public ResponseDto<Void> updateCartItemQuantity(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long cartItemId,
            @RequestBody UpdateCartItemQuantityRequest request
    ) {
        cartService.updateItemQuantity(user.getId(), cartItemId, request.getQuantity());
        return ResponseDto.ok(null);
    }

    // 장바구니 아이템 삭제
    @DeleteMapping("/items/{cartItemId}")
    public ResponseDto<Void> deleteCartItem(@AuthenticationPrincipal CustomUserDetails user, @PathVariable Long cartItemId) {
        cartService.removeItem(user.getId(), cartItemId);
        return ResponseDto.ok(null);
    }
}