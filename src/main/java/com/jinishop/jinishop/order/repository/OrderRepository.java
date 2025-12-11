package com.jinishop.jinishop.order.repository;

import com.jinishop.jinishop.order.domain.Order;
import com.jinishop.jinishop.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUser(User user);

    // 사용자별 주문 + 아이템 + 옵션 + 상품까지 한 번에 조회
    @Query("""
        select distinct o from Order o
        join fetch o.orderItems oi
        join fetch oi.productOption po
        join fetch po.product p
        where o.user = :user
        """)
    List<Order> findWithItemsByUser(@Param("user") User user);

    // 단일 주문 + 아이템 + 옵션 + 상품
    @Query("""
        select distinct o from Order o
        join fetch o.orderItems oi
        join fetch oi.productOption po
        join fetch po.product p
        where o.id = :orderId
        """)
    Optional<Order> findWithItemsById(@Param("orderId") Long orderId);
}