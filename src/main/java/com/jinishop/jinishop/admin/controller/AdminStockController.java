package com.jinishop.jinishop.admin.controller;

import com.jinishop.jinishop.stock.domain.Stock;
import com.jinishop.jinishop.stock.dto.StockAdjustRequest;
import com.jinishop.jinishop.stock.dto.StockResponse;
import com.jinishop.jinishop.stock.repository.StockRepository;
import com.jinishop.jinishop.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/stocks")
public class AdminStockController {

    private final StockRepository stockRepository;
    private final StockService stockService;

    // 관리자용 전체 재고 목록 조회
    @GetMapping
    public List<StockResponse> getAllStocks() {
        List<Stock> stocks = stockRepository.findAll();
        return stocks.stream()
                .map(StockResponse::new)
                .toList();
    }

    // 관리자용 재고 수동 조정
    @PatchMapping("/{productOptionId}")
    public void adjustStock(
            @PathVariable Long productOptionId,
            @RequestBody StockAdjustRequest request
    ) {
        stockService.adjustStockManually(productOptionId, request.getChangeAmount());
    }
}