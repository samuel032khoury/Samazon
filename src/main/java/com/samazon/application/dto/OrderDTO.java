package com.samazon.application.dto;

import java.time.LocalDate;
import java.util.List;

import org.modelmapper.ModelMapper;

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

    public static OrderDTO fromEntity(Order order, ModelMapper modelMapper) {
        OrderDTO orderDTO = modelMapper.map(order, OrderDTO.class);
        orderDTO.setUserId(order.getUser().getId());
        orderDTO.setAddressId(order.getAddress().getId());
        orderDTO.setPaymentId(order.getPayment().getId());
        return orderDTO;
    }
}
