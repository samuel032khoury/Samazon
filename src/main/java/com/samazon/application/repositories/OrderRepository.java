package com.samazon.application.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.samazon.application.models.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
}
