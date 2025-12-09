package com.jinishop.jinishop.admin.controller;

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
    public ProductResponse createProduct(@RequestBody ProductCreateRequest request) {
        // 상태 값이 없으면 기본 STOP_SALE
        ProductStatus status = ProductStatus.STOP_SALE;
        if (request.getStatus() != null) {
            status = ProductStatus.valueOf(request.getStatus());
        }

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .status(status)
                .build();

        Product saved = productService.saveProduct(product);
        return new ProductResponse(saved);
    }
}