package com.jinishop.jinishop.product.repository;

import com.jinishop.jinishop.product.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}