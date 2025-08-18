package com.samazon.application.services;

import com.samazon.application.dto.orders.OrderRequest;
import com.samazon.application.dto.orders.OrderResponse;

import jakarta.transaction.Transactional;

public interface OrderService {
    @Transactional
    OrderResponse createOrder(OrderRequest request);
}
