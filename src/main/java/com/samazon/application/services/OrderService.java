package com.samazon.application.services;

import java.util.List;

import com.samazon.application.dto.orders.OrderRequest;
import com.samazon.application.dto.orders.OrderResponse;

import jakarta.transaction.Transactional;

public interface OrderService {
    @Transactional
    OrderResponse createOrder(OrderRequest request);

    List<OrderResponse> getAllOrders();

    List<OrderResponse> getOrdersByUserId(Long userId);

    List<OrderResponse> getOrdersBySellerId(Long sellerId);

    OrderResponse getOrderById(Long orderId);

    OrderResponse updateOrderStatus(Long orderId, String status);

    void cancelOrder(Long orderId);

    void refundOrder(Long orderId);

    void deleteOrder(Long orderId);
}
