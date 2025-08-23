package com.samazon.application.services;

import java.util.List;

import com.samazon.application.dto.orders.OrderRequest;
import com.samazon.application.dto.orders.OrderResponse;
import com.samazon.application.models.User;
import com.samazon.application.models.enums.OrderStatus;

import jakarta.transaction.Transactional;

public interface OrderService {
    @Transactional
    OrderResponse createOrder(User user, OrderRequest request);

    List<OrderResponse> getAllOrders();

    List<OrderResponse> getOrdersByUser(User user);

    OrderResponse getOrderById(Long orderId);

    OrderResponse updateOrderStatus(Long orderId, OrderStatus status);

    void cancelOrder(Long orderId);

    void refundOrder(Long orderId);

    void deleteOrder(Long orderId);

    void checkPermission(User user, Long orderId);
}
