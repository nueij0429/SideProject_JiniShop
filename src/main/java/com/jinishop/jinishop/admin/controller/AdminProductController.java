package com.jinishop.jinishop.admin.controller;

import com.jinishop.jinishop.global.exception.BusinessException;
import com.jinishop.jinishop.global.exception.ErrorCode;
import com.jinishop.jinishop.global.response.ResponseDto;
import com.jinishop.jinishop.product.domain.Product;
import com.jinishop.jinishop.product.domain.ProductStatus;
import com.jinishop.jinishop.product.dto.ProductCreateRequest;
import com.jinishop.jinishop.product.dto.ProductResponse;
import com.jinishop.jinishop.product.dto.ProductUpdateRequest;
import com.jinishop.jinishop.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/products")
public class AdminProductController {

    private final ProductService productService;

    // 관리자용 - 상품 등록
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseDto<ProductResponse> createProduct(@RequestBody ProductCreateRequest request) {
        // 상태 값이 없으면 기본 STOP_SALE
        ProductStatus status = resolveStatusOrDefault(request.getStatus());

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .status(status)
                .build();

        Product saved = productService.saveProduct(product);
        return ResponseDto.ok(new ProductResponse(saved));
    }

    // 관리자용 - 상품 수정
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{productId}")
    public ResponseDto<ProductResponse> updateProduct(
            @PathVariable Long productId,
            @RequestBody ProductUpdateRequest request
    ) {
        ProductStatus status = null;
        if (request.getStatus() != null) {
            status = resolveStatusOrDefault(request.getStatus());
        }

        Product updated = productService.updateProduct(
                productId,
                request.getName(),
                request.getDescription(),
                request.getPrice(),
                status
        );

        return ResponseDto.ok(new ProductResponse(updated));
    }

    // 관리자용 - 상품 삭제
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{productId}")
    public ResponseDto<Void> deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);
        return ResponseDto.ok(null);
    }

    // status 문자열 → enum 변환 + 기본값 처리
    private ProductStatus resolveStatusOrDefault(String rawStatus) {
        if (rawStatus == null) {
            return ProductStatus.STOP_SALE;   // 기본값
        }
        try {
            return ProductStatus.valueOf(rawStatus);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ErrorCode.PRODUCT_STATUS_INVALID);
        }
    }
}