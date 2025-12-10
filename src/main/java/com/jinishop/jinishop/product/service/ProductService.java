package com.jinishop.jinishop.product.service;

import com.jinishop.jinishop.global.exception.BusinessException;
import com.jinishop.jinishop.global.exception.ErrorCode;
import com.jinishop.jinishop.product.domain.Product;
import com.jinishop.jinishop.product.domain.ProductOption;
import com.jinishop.jinishop.product.repository.ProductOptionRepository;
import com.jinishop.jinishop.product.repository.ProductRepository;
import com.jinishop.jinishop.stock.domain.Stock;
import com.jinishop.jinishop.stock.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductOptionRepository productOptionRepository;
    private final StockRepository stockRepository;

    // 전체 상품 목록 조회
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // 단일 상품 조회
    public Product getProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND)); // 상품을 찾을 수 없을 때
    }

    // 특정 상품의 옵션 목록 조회
    public List<ProductOption> getOptionsByProduct(Long productId) {
        Product product = getProduct(productId);
        return productOptionRepository.findByProduct(product);
    }

    // 특정 옵션의 재고 조회
    public Stock getStockByOption(Long productOptionId) {
        ProductOption option = productOptionRepository.findById(productOptionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_OPTION_NOT_FOUND)); // 상품 옵션을 찾을 수 없을 때

        return stockRepository.findByProductOption(option)
                .orElseThrow(() -> new BusinessException(ErrorCode.STOCK_NOT_FOUND)); // 해당 옵션의 재고를 찾을 수 없을 때
    }

    // 상품 추가 (쓰기 트랜잭션)
    @Transactional
    public Product saveProduct(Product product) {
        // 상품 이름 검증
        if (product.getName() == null || product.getName().isBlank()) {
            throw new BusinessException(ErrorCode.PRODUCT_NAME_REQUIRED); // 상품 이름을 입력 안 했을 시
        }

        // 가격 검증
        if (product.getPrice() == null) {
            throw new BusinessException(ErrorCode.PRODUCT_PRICE_REQUIRED); // 가격을 입력 안 했을 시
        }
        if (product.getPrice() <= 0) {
            throw new BusinessException(ErrorCode.PRODUCT_PRICE_INVALID); // 유효한 가격이 아닐 시
        }

        return productRepository.save(product);
    }
}
