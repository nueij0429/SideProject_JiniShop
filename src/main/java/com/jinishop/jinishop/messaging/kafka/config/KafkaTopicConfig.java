package com.jinishop.jinishop.messaging.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {
    // Kafka 토픽을 코드로 관리하는 설정 클래스

    @Value("${jinishop.messaging.kafka.order-events-topic}")
    private String topic;

    @Bean
    public NewTopic orderEventsTopic() {
        return TopicBuilder.name(topic)
                .partitions(3)
                .replicas(1)
                .build();
    }
}