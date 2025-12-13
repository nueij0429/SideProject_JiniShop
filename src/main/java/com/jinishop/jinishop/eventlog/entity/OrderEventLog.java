package com.jinishop.jinishop.eventlog.entity;

import com.jinishop.jinishop.messaging.kafka.dto.EventType;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "order_event_log",
        indexes = {
                @Index(name = "idx_event_order_id", columnList = "order_id"),
                @Index(name = "idx_event_type", columnList = "event_type")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_event_id", columnNames = "event_id")
        }
)
public class OrderEventLog {
    // Kafka로 흘러간 이벤트를 DB에도 영속화하기 위한 이벤트 로그 엔티티

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // unique를 사용해 중복 소비 방지
    @Column(name="event_id", nullable = false, unique = true, length = 36)
    private String eventId;

    @Enumerated(EnumType.STRING)
    @Column(name="event_type", nullable = false, length = 30)
    private EventType eventType;

    @Column(name="order_id", nullable = false)
    private Long orderId;

    @Column(name="user_id", nullable = false)
    private Long userId;

    @Column(name="total_amount", nullable = false)
    private Long totalAmount;

    @Column(name="detail", length = 255)
    private String detail;

    @Column(name="occurred_at", nullable = false)
    private LocalDateTime occurredAt;

    // 코드 단순화
    public static OrderEventLog from(UUID eventId, EventType type, Long orderId, Long userId,
                                     Long totalAmount, String detail, LocalDateTime occurredAt) {
        return OrderEventLog.builder()
                .eventId(eventId.toString())
                .eventType(type)
                .orderId(orderId)
                .userId(userId)
                .totalAmount(totalAmount)
                .detail(detail)
                .occurredAt(occurredAt)
                .build();
    }
}