package com.samazon.application.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.samazon.application.dto.orders.OrderRequest;
import com.samazon.application.dto.orders.OrderResponse;
import com.samazon.application.services.OrderService;
import com.samazon.application.utils.AuthUtil;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class OrderController {

    private OrderService orderService;

    private final AuthUtil authUtil;

    @PostMapping("/orders")
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest request) {
        OrderResponse response = orderService.createOrder(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/admin/audit/orders")
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        List<OrderResponse> responses = orderService.getAllOrders();
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @GetMapping("/user/orders")
    public ResponseEntity<List<OrderResponse>> getUserOrders() {
        List<OrderResponse> responses = orderService.getOrdersByUserId(authUtil.getCurrentUser().getId());
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @GetMapping("/user/orders/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long orderId) {
        OrderResponse response = orderService.getOrderById(orderId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
