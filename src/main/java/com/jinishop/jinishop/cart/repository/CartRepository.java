package com.jinishop.jinishop.cart.repository;

import com.jinishop.jinishop.cart.domain.Cart;
import com.jinishop.jinishop.cart.domain.CartItem;
import com.jinishop.jinishop.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByUser(User user);

    // CartItem + ProductOption + Product 한 번에 조회
    @Query("""
        select ci from CartItem ci
        join fetch ci.productOption po
        join fetch po.product p
        where ci.cart = :cart
        """)
    List<CartItem> findByCartWithProduct(@Param("cart") Cart cart);
}
