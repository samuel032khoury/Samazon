package com.samazon.application.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.samazon.application.models.Cart;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByUserId(Long userId);

    List<Cart> findByCartItemsProductId(Long productId);

    List<Cart> findByCartItemsProductCategoryId(Long categoryId);
}
