package com.samazon.application.dto;

import java.time.LocalDate;
import java.util.List;

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
}
