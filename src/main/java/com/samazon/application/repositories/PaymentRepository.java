package com.samazon.application.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.samazon.application.models.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
