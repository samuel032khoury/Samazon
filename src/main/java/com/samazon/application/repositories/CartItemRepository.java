package com.samazon.application.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.samazon.application.models.CartItem;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    public Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);

    public Optional<CartItem> findByProductIdAndCartUserId(Long productId, Long userId);
}
