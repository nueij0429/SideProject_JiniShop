package com.jinishop.jinishop.messaging.kafka.producer;

import com.jinishop.jinishop.messaging.kafka.dto.OrderEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderEventProducer {
    // 주문/결제 이벤트를 Kafka로 발행하는 Producer

    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    @Value("${jinishop.messaging.kafka.order-events-topic}")
    private String topic;

    public void publish(OrderEvent event) {
        // key를 orderId로 주면 같은 주문 이벤트가 같은 파티션에 모이기 쉬움
        String key = String.valueOf(event.getOrderId());
        kafkaTemplate.send(topic, key, event); // 이벤트 발생 사실을 Kafka에 기록
    }
}