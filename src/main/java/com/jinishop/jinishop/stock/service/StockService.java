package com.jinishop.jinishop.stock.service;

import com.jinishop.jinishop.global.exception.BusinessException;
import com.jinishop.jinishop.global.exception.ErrorCode;
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

    // 재고 감소 기본 버전 - 낙관적 락 사용: @Version
    @Transactional
    public void decreaseStock(Long productOptionId, int quantity) {
        decreaseStockInternal(productOptionId, quantity, StockHistoryReason.ORDER, false); // 재고 이력에는 ORDER 라는 변경 사유가 기록됨
    }

    // 비관적 락 버전
    @Transactional
    public void decreaseStockWithPessimisticLock(Long productOptionId, int quantity) {
        decreaseStockInternal(productOptionId, quantity, StockHistoryReason.ORDER, true);
    }

    // 관리자 재고 수동 조정 - 낙관적 락
    @Transactional
    public void adjustStockManually(Long productOptionId, int changeAmount) {
        if (changeAmount == 0) {
            throw new BusinessException(ErrorCode.STOCK_ADJUST_AMOUNT_INVALID); // 유효한 재고 수정 값이 아닐 때
        }

        if (changeAmount > 0) {
            increaseStock(productOptionId, changeAmount, StockHistoryReason.MANUAL_ADJUST, false);
        } else {
            // 음수일 때는 절댓값만큼 감소
            decreaseStockInternal(productOptionId, -changeAmount, StockHistoryReason.MANUAL_ADJUST, false);
        }
    }

    // 중복을 막고 로직 재사용을 위해 decreaseStockInternal()로 분리해둠
    // 내부용 재고 감소 로직
    @Transactional
    protected void decreaseStockInternal(
            Long productOptionId,
            int quantity,
            StockHistoryReason reason,
            boolean usePessimisticLock
    ) {
        // 옵션 ID로 ProductOption 조회
        ProductOption option = productOptionRepository.findById(productOptionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_OPTION_NOT_FOUND)); // 상품 옵션을 찾을 수 없을 때

        // 옵션으로 Stock 조회
        Stock stock = (usePessimisticLock
                ? stockRepository.findWithLockByProductOption(option)
                : stockRepository.findByProductOption(option))
                .orElseThrow(() -> new BusinessException(ErrorCode.STOCK_NOT_FOUND)); // 재고 정보를 찾을 수 없을 때

        if (stock.getQuantity() < quantity) {
            throw new BusinessException(ErrorCode.STOCK_NOT_ENOUGH); // 재고 부족 시
        }

        // 문제 없으면 재고 차감
        stock.setQuantity(stock.getQuantity() - quantity);

        // StockHistory 기록 저장
        stockHistoryRepository.save(
                StockHistory.builder()
                    .productOption(option)
                    .changeAmount(-quantity)
                    .reason(reason)
                    .build()
        );
    }

    // 내부용 재고 증가 로직
    @Transactional
    protected void increaseStock(
            Long productOptionId,
            int quantity,
            StockHistoryReason reason,
            boolean usePessimisticLock
    ) {
        // 옵션 ID로 ProductOption 조회
        ProductOption option = productOptionRepository.findById(productOptionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_OPTION_NOT_FOUND)); // 상품 옵션을 찾을 수 없을 때

        // 옵션으로 Stock 조회
        Stock stock = stockRepository.findByProductOption(option)
                .orElseThrow(() -> new BusinessException(ErrorCode.STOCK_NOT_FOUND)); // 재고 정보를 찾을 수 없을 때

        // 재고 증가
        stock.setQuantity(stock.getQuantity() + quantity);

        // StockHistory 기록 저장
        stockHistoryRepository.save(
                StockHistory.builder()
                        .productOption(option)
                        .changeAmount(-quantity)
                        .reason(reason)
                        .build()
        );
    }
}