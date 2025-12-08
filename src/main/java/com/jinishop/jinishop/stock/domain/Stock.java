package com.jinishop.jinishop.stock.domain;

import com.jinishop.jinishop.product.domain.ProductOption;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "stock")
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // product_option과 1:1 관계 (UNIQUE 제약)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_option_id", nullable = false, unique = true)
    private ProductOption productOption;

    @Column(nullable = false)
    private Integer quantity;

    // 낙관적 락 버전
    @Version
    @Column(nullable = false)
    private Integer version;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;
}
