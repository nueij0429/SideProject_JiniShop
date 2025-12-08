package com.jinishop.jinishop.order.repository;

import com.jinishop.jinishop.order.domain.Order;
import com.jinishop.jinishop.order.domain.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrder(Order order);
}