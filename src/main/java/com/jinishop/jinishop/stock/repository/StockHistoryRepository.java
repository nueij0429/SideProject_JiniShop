package com.jinishop.jinishop.stock.repository;

import com.jinishop.jinishop.product.domain.ProductOption;
import com.jinishop.jinishop.stock.domain.StockHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockHistoryRepository extends JpaRepository<StockHistory, Long> {

    List<StockHistory> findByProductOption(ProductOption productOption);
}