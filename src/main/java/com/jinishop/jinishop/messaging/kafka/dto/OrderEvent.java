package com.jinishop.jinishop.messaging.kafka.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderEvent {
    // Kafka에 기록되는 Fact 단위 이벤트 DTO

    private UUID eventId;   // 이벤트 단위 식별자 (중복 소비 방지)
    private EventType type;

    private Long orderId;
    private Long userId;
    private Long totalAmount;   // 금액 로그

    private String detail;  // 실패 사유 / 성공 메시지
    private LocalDateTime occurredAt;   // 실제 발생 시각
}