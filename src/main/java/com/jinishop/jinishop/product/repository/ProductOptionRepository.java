package com.jinishop.jinishop.product.repository;

import com.jinishop.jinishop.product.domain.Product;
import com.jinishop.jinishop.product.domain.ProductOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductOptionRepository extends JpaRepository<ProductOption, Long> {

    List<ProductOption> findByProduct(Product product);
}