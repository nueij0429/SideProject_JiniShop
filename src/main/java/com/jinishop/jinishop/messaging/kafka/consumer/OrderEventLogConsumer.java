package com.jinishop.jinishop.messaging.kafka.consumer;

import com.jinishop.jinishop.eventlog.entity.OrderEventLog;
import com.jinishop.jinishop.eventlog.repository.OrderEventLogRepository;
import com.jinishop.jinishop.messaging.kafka.dto.OrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventLogConsumer {
    // Kafka에 흘러온 주문/결제 이벤트를 DB에 감사 로그로 저장하는 Consumer

    private final OrderEventLogRepository repository;

    // Kafka 메시지 수신
    @KafkaListener(
            topics = "${jinishop.messaging.kafka.order-events-topic}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    @Transactional
    public void consume(OrderEvent event) {
        try {
            repository.save(OrderEventLog.from( // Kafka DTO → JPA Entity 변환
                    event.getEventId(),
                    event.getType(),
                    event.getOrderId(),
                    event.getUserId(),
                    event.getTotalAmount(),
                    event.getDetail(),
                    event.getOccurredAt()
            ));
            log.info("[KAFKA-LOG] saved event: type={}, orderId={}", event.getType(), event.getOrderId());
        } catch (DataIntegrityViolationException e) {
            // uk_event_id로 중복 저장 방지
            log.warn("[KAFKA-LOG] duplicate event ignored: eventId={}", event.getEventId());
        }
    }
}