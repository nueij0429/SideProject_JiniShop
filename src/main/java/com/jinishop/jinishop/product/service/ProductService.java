package com.jinishop.jinishop.product.service;

import com.jinishop.jinishop.global.exception.BusinessException;
import com.jinishop.jinishop.global.exception.ErrorCode;
import com.jinishop.jinishop.product.domain.Product;
import com.jinishop.jinishop.product.domain.ProductOption;
import com.jinishop.jinishop.product.domain.ProductStatus;
import com.jinishop.jinishop.product.repository.ProductOptionRepository;
import com.jinishop.jinishop.product.repository.ProductRepository;
import com.jinishop.jinishop.stock.domain.Stock;
import com.jinishop.jinishop.stock.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductOptionRepository productOptionRepository;
    private final StockRepository stockRepository;

    // 전체 상품 목록 조회 - 읽기 많은 API이기 때문에 Redis 캐시 사용
    @Cacheable(value = "products", key = "'all'")
    public List<Product> getAllProducts() {
        log.info(">>> getAllProducts() called");
        // 캐시 미스 시에만 DB 조회
        return productRepository.findAll();
    }

    // 단일 상품 조회 - 읽기 많은 API이기 때문에 Redis 캐시 사용
    @Cacheable(cacheNames = "products", key = "#productId")
    public Product getProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND)); // 상품을 찾을 수 없을 때
    }

    // 특정 상품의 옵션 목록 조회
    public List<ProductOption> getOptionsByProduct(Long productId) {
        Product product = getProduct(productId);
        return productOptionRepository.findByProduct(product);
    }

    // 특정 옵션의 재고 조회 - 재고/주문은 강한 일관성이 중요하므로 캐시 사용 X
    public Stock getStockByOption(Long productOptionId) {
        ProductOption option = productOptionRepository.findById(productOptionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_OPTION_NOT_FOUND)); // 상품 옵션을 찾을 수 없을 때

        return stockRepository.findByProductOption(option)
                .orElseThrow(() -> new BusinessException(ErrorCode.STOCK_NOT_FOUND)); // 해당 옵션의 재고를 찾을 수 없을 때
    }

    // 상품 추가 (쓰기 트랜잭션) - DB에 쓰기 성공 시, 관련 상품 캐시는 모두 삭제
    //                         & 읽기 요청에서 DB 재조회 후 캐시에 다시 채움 (cache-aside)
    @Transactional
    @CacheEvict(cacheNames = "products", allEntries = true)
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

    // 관리자용 - 상품 수정
    @Transactional
    @CacheEvict(cacheNames = "products", allEntries = true)
    public Product updateProduct(Long productId,
                                 String name,
                                 String description,
                                 Long price,
                                 ProductStatus status) {

        Product product = getProduct(productId);

        // 상품 이름 검증
        if (name == null || name.isBlank()) {
            throw new BusinessException(ErrorCode.PRODUCT_NAME_REQUIRED);
        } // 가격 검증
        if (price == null) {
            throw new BusinessException(ErrorCode.PRODUCT_PRICE_REQUIRED);
        }
        if (price <= 0) {
            throw new BusinessException(ErrorCode.PRODUCT_PRICE_INVALID);
        }

        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        if (status != null) {
            product.setStatus(status);
        }

        // JPA 영속 상태라 save() 안 해도 flush 시점에 반영
        return product;
    }

    // 관리자용 - 상품 삭제
    @Transactional
    @CacheEvict(cacheNames = "products", allEntries = true)
    public void deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

        productRepository.delete(product);
    }

}
