package com.jinishop.jinishop.messaging.rabbit.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequestedMessage {
    // RabbitMQ로 전달되는 결제 요청 이벤트 DTO

    private UUID eventId;
    private Long orderId;
    private Long userId;
    private Long totalAmount;
    private LocalDateTime requestedAt;
}
