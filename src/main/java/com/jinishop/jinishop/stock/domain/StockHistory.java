package com.jinishop.jinishop.stock.domain;

import com.jinishop.jinishop.product.domain.ProductOption;
import com.jinishop.jinishop.stock.domain.StockHistoryReason;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "stock_history")
public class StockHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_option_id", nullable = false)
    private ProductOption productOption;

    @Column(name = "change_amount", nullable = false)
    private Integer changeAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StockHistoryReason reason;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;
}
