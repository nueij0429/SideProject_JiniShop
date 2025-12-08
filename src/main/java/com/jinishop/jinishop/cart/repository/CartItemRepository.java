package com.jinishop.jinishop.cart.repository;

import com.jinishop.jinishop.cart.domain.Cart;
import com.jinishop.jinishop.cart.domain.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByCart(Cart cart);
}