package com.jinishop.jinishop.admin.controller;

import com.jinishop.jinishop.global.exception.BusinessException;
import com.jinishop.jinishop.global.exception.ErrorCode;
import com.jinishop.jinishop.global.response.ResponseDto;
import com.jinishop.jinishop.product.domain.Product;
import com.jinishop.jinishop.product.domain.ProductStatus;
import com.jinishop.jinishop.product.dto.ProductCreateRequest;
import com.jinishop.jinishop.product.dto.ProductResponse;
import com.jinishop.jinishop.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/products")
public class AdminProductController {

    private final ProductService productService;

    // 관리자용 상품 등록
    @PostMapping
    public ResponseDto<ProductResponse> createProduct(@RequestBody ProductCreateRequest request) {
        // 상태 값이 없으면 기본 STOP_SALE
        ProductStatus status = ProductStatus.STOP_SALE;
        if (request.getStatus() != null) {
            try {
                status = ProductStatus.valueOf(request.getStatus());
            } catch (IllegalArgumentException e) {
                throw new BusinessException(ErrorCode.PRODUCT_STATUS_INVALID);
            }
        }

        // 서비스 로직으로 분리할 예정
        if (request.getName() == null || request.getName().isBlank()) {
            throw new BusinessException(ErrorCode.PRODUCT_NAME_REQUIRED);
        }
        if (request.getPrice() == null) {
            throw new BusinessException(ErrorCode.PRODUCT_PRICE_REQUIRED);
        }
        if (request.getPrice() <= 0) {
            throw new BusinessException(ErrorCode.PRODUCT_PRICE_INVALID);
        }

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .status(status)
                .build();

        Product saved = productService.saveProduct(product);
        return ResponseDto.ok(new ProductResponse(saved));
    }
}