package com.samazon.application.dto;

import java.time.LocalDate;
import java.util.List;

import com.samazon.application.models.Order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private Long id;
    private Long userId;
    private List<OrderItemDTO> orderItems;
    private LocalDate orderDate;
    private Long paymentId;
    private Double totalAmount;
    private String orderStatus;
    private Long addressId;

    public static OrderDTO fromEntity(Order order) {
        return new OrderDTO(
                order.getId(),
                order.getUser() != null ? order.getUser().getId() : null,
                order.getOrderItems().stream().map(OrderItemDTO::fromEntity).toList(),
                order.getOrderDate(),
                order.getPayment() != null ? order.getPayment().getId() : null,
                order.getTotalAmount(),
                order.getOrderStatus(),
                order.getAddress() != null ? order.getAddress().getId() : null);
    }
}
