package com.jinishop.jinishop.product.controller;

import com.jinishop.jinishop.product.domain.Product;
import com.jinishop.jinishop.product.domain.ProductOption;
import com.jinishop.jinishop.product.dto.ProductOptionResponse;
import com.jinishop.jinishop.product.dto.ProductResponse;
import com.jinishop.jinishop.product.service.ProductService;
import com.jinishop.jinishop.stock.domain.Stock;
import com.jinishop.jinishop.stock.dto.StockResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    // 전체 상품 목록 조회
    @GetMapping
    public List<ProductResponse> getProducts() {
        List<Product> products = productService.getAllProducts();
        return products.stream()
                .map(ProductResponse::new)
                .toList();
    }

    // 상품 단건 조회
    @GetMapping("/{productId}")
    public ProductResponse getProduct(@PathVariable Long productId) {
        Product product = productService.getProduct(productId);
        return new ProductResponse(product);
    }

    // 특정 상품의 옵션 목록 조회
    @GetMapping("/{productId}/options")
    public List<ProductOptionResponse> getProductOptions(@PathVariable Long productId) {
        List<ProductOption> options = productService.getOptionsByProduct(productId);
        return options.stream()
                .map(ProductOptionResponse::new)
                .toList();
    }

    // 특정 옵션의 재고 조회
    @GetMapping("/options/{optionId}/stock")
    public StockResponse getStock(@PathVariable Long optionId) {
        Stock stock = productService.getStockByOption(optionId);
        return new StockResponse(stock);
    }
}
