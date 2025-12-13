package com.jinishop.jinishop.messaging.rabbit.publisher;

import com.jinishop.jinishop.messaging.rabbit.dto.PaymentRequestedMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentMessagePublisher {
    // 결제 요청을 RabbitMQ로 발행하는 Producer

    private final RabbitTemplate rabbitTemplate;

    @Value("${jinishop.messaging.rabbit.payment.exchange}")
    private String exchange;

    @Value("${jinishop.messaging.rabbit.payment.routing-key}")
    private String routingKey;

    public void publishPaymentRequested(PaymentRequestedMessage msg) {
        rabbitTemplate.convertAndSend(exchange, routingKey, msg); // 비즈니스 로직에서 메시지 발행 책임 분리
    }
}