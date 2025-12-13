package com.jinishop.jinishop.messaging.rabbit.consumer;

import java.time.LocalDateTime;
import java.util.Random;

import com.jinishop.jinishop.global.exception.BusinessException;
import com.jinishop.jinishop.global.exception.ErrorCode;
import com.jinishop.jinishop.messaging.kafka.dto.EventType;
import com.jinishop.jinishop.messaging.kafka.dto.OrderEvent;
import com.jinishop.jinishop.messaging.kafka.producer.OrderEventProducer;
import com.jinishop.jinishop.messaging.rabbit.dto.PaymentRequestedMessage;
import com.jinishop.jinishop.order.domain.Order;
import com.jinishop.jinishop.order.domain.OrderStatus;
import com.jinishop.jinishop.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentRequestConsumer {
    // RabbitMQ 결제 요청 메시지를 소비하고, 결제 결과를 Kafka 이벤트로 남기는 Consumer

    private final OrderRepository orderRepository;
    private final OrderEventProducer orderEventProducer;

    private final Random random = new Random();

    // 메시지 소비 + DB 작업을 하나의 트랜잭션으로 묶음
    @RabbitListener(
            queues = "${jinishop.messaging.rabbit.payment.queue}",
            containerFactory = "rabbitListenerContainerFactory"
    )
    @Transactional
    public void handle(PaymentRequestedMessage msg) {
        log.info("[PAYMENT] received: orderId={}, userId={}, amount={}",
                msg.getOrderId(), msg.getUserId(), msg.getTotalAmount());

        Order order = orderRepository.findById(msg.getOrderId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        // 결제 처리 중 상태
        order.setStatus(OrderStatus.PAYMENT_PENDING);

        // 외부 결제처럼 느리게
        try {
            Thread.sleep(200 + random.nextInt(300)); // 200~500ms
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }

        // 결제 로직이 없기 때문에 시뮬레이션으로 구현함
        // 90% 성공, 10% 실패 시뮬레이션 - 외부 PG 호출을 가정
        boolean success = random.nextInt(10) < 9;

        if (success) {
            order.setStatus(OrderStatus.PAYMENT_SUCCESS);

            orderEventProducer.publish(OrderEvent.builder()
                    .eventId(java.util.UUID.randomUUID())
                    .type(EventType.PAYMENT_SUCCEEDED)
                    .orderId(order.getId())
                    .userId(order.getUser().getId())
                    .totalAmount(order.getTotalAmount())
                    .detail("결제 성공! (시뮬레이션)")
                    .occurredAt(LocalDateTime.now())
                    .build());

            log.info("[PAYMENT] success: orderId={}", order.getId());
        } else {
            order.setStatus(OrderStatus.PAYMENT_FAILED);

            // Kafka 이벤트 발행 - 트랜잭션 안에서 바로 Kafka 발행
            // TxAfterCommitExecutor와 함께 쓰는 코드로 바꾸면 실무적!
            orderEventProducer.publish(OrderEvent.builder()
                    .eventId(java.util.UUID.randomUUID())
                    .type(EventType.PAYMENT_FAILED)
                    .orderId(order.getId())
                    .userId(order.getUser().getId())
                    .totalAmount(order.getTotalAmount())
                    .detail("결제 실패! (시뮬레이션)")
                    .occurredAt(LocalDateTime.now())
                    .build());

            log.warn("[PAYMENT] failed: orderId={}", order.getId());

            // 실패를 DLQ로 보내고 싶으면 예외를 던지면 됨 (재시도 후 DLQ)
            // 지금은 실패도 정상 결과로 처리하니까 예외 던지지 않음
        }
    }
}