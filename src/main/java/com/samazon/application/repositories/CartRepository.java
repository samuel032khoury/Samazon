package com.samazon.application.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.samazon.application.models.Cart;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    Cart findByUserId(Long userId);

}
