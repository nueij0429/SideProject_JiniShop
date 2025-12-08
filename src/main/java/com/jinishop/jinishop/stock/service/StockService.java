package com.jinishop.jinishop.stock.service;

import com.jinishop.jinishop.product.domain.ProductOption;
import com.jinishop.jinishop.product.repository.ProductOptionRepository;
import com.jinishop.jinishop.stock.domain.Stock;
import com.jinishop.jinishop.stock.domain.StockHistory;
import com.jinishop.jinishop.stock.domain.StockHistoryReason;
import com.jinishop.jinishop.stock.repository.StockHistoryRepository;
import com.jinishop.jinishop.stock.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;
    private final ProductOptionRepository productOptionRepository;
    private final StockHistoryRepository stockHistoryRepository;

    // 재고 감소 (나중에 락 적용 버전으로 확장할 예정)
    @Transactional
    public void decreaseStock(Long productOptionId, int quantity) {
        ProductOption option = productOptionRepository.findById(productOptionId)
                .orElseThrow(() -> new NoSuchElementException("상품 옵션을 찾을 수 없음. id=" + productOptionId));

        Stock stock = stockRepository.findByProductOption(option)
                .orElseThrow(() -> new NoSuchElementException("재고 정보를 찾을 수 없음. optionId=" + productOptionId));

        if (stock.getQuantity() < quantity) {
            throw new IllegalStateException("재고 부족. 요청수량=" + quantity + ", 현재재고=" + stock.getQuantity());
        }

        stock.setQuantity(stock.getQuantity() - quantity);

        // 재고 히스토리 기록
        StockHistory history = StockHistory.builder()
                .productOption(option)
                .changeAmount(-quantity)
                .reason(StockHistoryReason.ORDER)
                .build();

        stockHistoryRepository.save(history);
    }

    // 재고 증가 (관리자 수동 조정)
    @Transactional
    public void increaseStock(Long productOptionId, int quantity, StockHistoryReason reason) {
        ProductOption option = productOptionRepository.findById(productOptionId)
                .orElseThrow(() -> new NoSuchElementException("상품 옵션을 찾을 수 없음. id=" + productOptionId));

        Stock stock = stockRepository.findByProductOption(option)
                .orElseThrow(() -> new NoSuchElementException("재고 정보를 찾을 수 없음. optionId=" + productOptionId));

        stock.setQuantity(stock.getQuantity() + quantity);

        StockHistory history = StockHistory.builder()
                .productOption(option)
                .changeAmount(quantity)
                .reason(reason)
                .build();

        stockHistoryRepository.save(history);
    }
}