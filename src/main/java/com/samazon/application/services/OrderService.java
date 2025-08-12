package com.samazon.application.services;

import com.samazon.application.dto.orders.OrderRequest;
import com.samazon.application.dto.orders.OrderDTO;

import jakarta.transaction.Transactional;

public interface OrderService {
    @Transactional
    OrderDTO placeOrder(OrderRequest request);
}
