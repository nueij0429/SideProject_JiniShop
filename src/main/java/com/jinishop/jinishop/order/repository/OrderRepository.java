package com.jinishop.jinishop.order.repository;

import com.jinishop.jinishop.order.domain.Order;
import com.jinishop.jinishop.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUser(User user);
}