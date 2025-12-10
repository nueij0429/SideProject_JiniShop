package com.jinishop.jinishop.stock.repository;

import com.jinishop.jinishop.product.domain.ProductOption;
import com.jinishop.jinishop.stock.domain.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import jakarta.persistence.LockModeType;
import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, Long> {

    Optional<Stock> findByProductOption(ProductOption productOption);

    // 나중에 동시성 실험용 (비관적 락) - 같은 row에 동시에 쓰기 요청이 오면 DB 레벨에서 순서대로 처리
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Stock> findWithLockByProductOption(ProductOption productOption);
}
