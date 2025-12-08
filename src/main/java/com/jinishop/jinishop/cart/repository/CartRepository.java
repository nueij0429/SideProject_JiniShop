package com.jinishop.jinishop.cart.repository;

import com.jinishop.jinishop.cart.domain.Cart;
import com.jinishop.jinishop.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByUser(User user);
}
