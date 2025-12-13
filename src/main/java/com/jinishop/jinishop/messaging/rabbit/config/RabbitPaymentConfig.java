package com.jinishop.jinishop.messaging.rabbit.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

@Configuration
public class RabbitPaymentConfig {
    // RabbitMQ 인프라 + 소비자(Listener) 동작 규칙을 정의하는 설정 클래스

    // 결제 요청용 Exchange / Queue / DLX / DLQ 생성
    @Value("${jinishop.messaging.rabbit.payment.exchange}")
    private String paymentExchange;

    @Value("${jinishop.messaging.rabbit.payment.routing-key}")
    private String paymentRoutingKey;

    @Value("${jinishop.messaging.rabbit.payment.queue}")
    private String paymentQueue;

    @Value("${jinishop.messaging.rabbit.payment.dlx}")
    private String dlx;

    @Value("${jinishop.messaging.rabbit.payment.dlq}")
    private String dlq;

    @Value("${jinishop.messaging.rabbit.payment.dlq-routing-key}")
    private String dlqRoutingKey;

    // 결제 요청 메시지를 받는 메인 Exchange
    @Bean
    public DirectExchange paymentExchange() {
        return new DirectExchange(paymentExchange);
    }

    // DLX 구성
    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(dlx);
    }

    // 실제 결제 요청을 소비하는 Queue
    @Bean
    public Queue paymentRequestQueue() {
        Map<String, Object> args = new HashMap<>();
        // 실패한 메시지는 DLX로 보냄
        args.put("x-dead-letter-exchange", dlx);
        args.put("x-dead-letter-routing-key", dlqRoutingKey);
        return new Queue(paymentQueue, true, false, false, args);
    }

    // DLQ 구성
    @Bean
    public Queue paymentRequestDlq() {
        return new Queue(dlq, true);
    }

    @Bean
    public Binding paymentBinding() {
        return BindingBuilder.bind(paymentRequestQueue())
                .to(paymentExchange())
                .with(paymentRoutingKey);
    }

    @Bean
    public Binding dlqBinding() {
        return BindingBuilder.bind(paymentRequestDlq())
                .to(deadLetterExchange())
                .with(dlqRoutingKey);
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter(ObjectMapper om) {
        return new Jackson2JsonMessageConverter(om);
    }

    // Listener 동작의 핵심 설정
    // Listener 컨테이너: JSON 변환 + retry(3회) + 실패 시 DLQ로 떨어지게
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter converter
    ) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(converter);

        // 재시도
        RetryTemplate retryTemplate = new RetryTemplate();
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy(3); // 총 3회 시도
        FixedBackOffPolicy backOff = new FixedBackOffPolicy();
        backOff.setBackOffPeriod(500); // 0.5초 간격

        retryTemplate.setRetryPolicy(retryPolicy);
        retryTemplate.setBackOffPolicy(backOff);

        factory.setRetryTemplate(retryTemplate);

        // 재시도 끝나도 실패하면 메시지는 reject되고 DLQ로 이동
        factory.setDefaultRequeueRejected(false);

        return factory;
    }
}