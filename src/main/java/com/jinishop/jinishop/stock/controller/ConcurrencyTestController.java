package com.jinishop.jinishop.stock.controller;

import com.jinishop.jinishop.global.exception.BusinessException;
import com.jinishop.jinishop.global.exception.ErrorCode;
import com.jinishop.jinishop.global.response.ResponseDto;
import com.jinishop.jinishop.product.domain.ProductOption;
import com.jinishop.jinishop.product.repository.ProductOptionRepository;
import com.jinishop.jinishop.stock.domain.Stock;
import com.jinishop.jinishop.stock.repository.StockRepository;
import com.jinishop.jinishop.stock.service.StockService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/test/concurrency")
public class ConcurrencyTestController {
    // 동시성 실험용 컨트롤러 - 실제 서비스에서는 사용 X

    private final StockService stockService;
    private final ProductOptionRepository productOptionRepository;
    private final StockRepository stockRepository;

    // 낙관적 락 기반 재고 차감 동시성 테스트
    @PostMapping("/optimistic")
    public ResponseDto<ConcurrencyResult> testOptimistic(@RequestBody ConcurrencyRequest request) throws InterruptedException {
        return ResponseDto.ok(runConcurrencyTest(request, false));
    }

    // 비관적 락 기반 재고 차감 동시성 테스트
    @PostMapping("/pessimistic")
    public ResponseDto<ConcurrencyResult> testPessimistic(@RequestBody ConcurrencyRequest request) throws InterruptedException {
        return ResponseDto.ok(runConcurrencyTest(request, true));
    }

    // 실제 멀티스레드 재고 차감 실험을 수행
    private ConcurrencyResult runConcurrencyTest(ConcurrencyRequest request, boolean usePessimisticLock)
            throws InterruptedException {

        // 초기 옵션 조회
        ProductOption option = productOptionRepository.findById(request.getProductOptionId())
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_OPTION_NOT_FOUND));

        // 초기 재고 조회
        Stock stock = stockRepository.findByProductOption(option)
                .orElseThrow(() -> new BusinessException(ErrorCode.STOCK_NOT_FOUND));

        // 스레드/요청 조건 설정
        int threadCount = request.getThreadCount();
        int decreaseQuantity = request.getQuantityPerThread();

        // 스레드 풀
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        // 각 스레드에서 발생한 예외 에러코드를 모아두는 리스트
        List<String> errors = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) { // 멀티스레드 재고 차감 수행
            executorService.submit(() -> {
                try {
                    if (usePessimisticLock) {
                        stockService.decreaseStockWithPessimisticLock(option.getId(), decreaseQuantity);
                    } else {
                        stockService.decreaseStock(option.getId(), decreaseQuantity);
                    }
                } catch (BusinessException e) {
                    // 재고 부족, 기타 비즈니스 예외 수집
                    synchronized (errors) {
                        errors.add(e.getErrorCode().name());
                    }
                } catch (Exception e) {
                    synchronized (errors) {
                        errors.add(e.getClass().getSimpleName() + ": " + e.getMessage());
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // 최종 재고 조회
        Stock finalStock = stockRepository.findByProductOption(option)
                .orElseThrow(() -> new BusinessException(ErrorCode.STOCK_NOT_FOUND));

        ConcurrencyResult result = new ConcurrencyResult();
        result.setInitialQuantity(stock.getQuantity());
        result.setFinalQuantity(finalStock.getQuantity());
        result.setThreadCount(threadCount);
        result.setQuantityPerThread(decreaseQuantity);
        result.setErrors(errors);

        return result;
    }

    // 요청/응답 DTO (실험용이라 내부 클래스로 둠)
    @Getter
    @Setter
    public static class ConcurrencyRequest {
        private Long productOptionId;   // 어떤 옵션 재고를 테스트할지
        private int threadCount;        // 동시에 몇 스레드 돌릴지
        private int quantityPerThread;  // 각 스레드가 몇 개씩 차감할지
    }

    @Getter
    @Setter
    public static class ConcurrencyResult {
        private int initialQuantity;
        private int finalQuantity;
        private int threadCount;
        private int quantityPerThread;
        private List<String> errors;
    }
}