package com.jinishop.jinishop.order.domain;

import com.jinishop.jinishop.order.domain.OrderStatus;
import com.jinishop.jinishop.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "`order`", // 예약어 백틱
        indexes = {
                @Index(name = "idx_order_user_id", columnList = "user_id")
                // 나중에 정렬 기준으로 created_at 쓸 때 사용
                // @Index(name = "idx_order_user_created_at", columnList = "user_id, created_at")
        }
)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 사용자별 주문 목록 조회 - user_id 인덱스 타게 됨
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "total_amount", nullable = false)
    private Long totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    // Order ↔ OrderItem 양방향 매핑으로 확장
    @OneToMany(mappedBy = "order")
    @Builder.Default // builder가 null 넣는 문제 해결
    private List<OrderItem> orderItems = new ArrayList<>();

    public void addOrderItem(OrderItem item) {
        this.orderItems.add(item);
        item.setOrder(this);
    }
}
