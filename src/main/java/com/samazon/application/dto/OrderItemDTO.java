package com.samazon.application.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import com.samazon.application.models.OrderItem;

import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDTO {
    private Long id;
    private Long productId;
    private Integer quantity;
    private Double discount;
    private Double priceAtOrder;

    public static OrderItemDTO fromEntity(OrderItem orderItem) {
        return new OrderItemDTO(
                orderItem.getId(),
                orderItem.getProduct() != null ? orderItem.getProduct().getId() : null,
                orderItem.getQuantity(),
                orderItem.getDiscount(),
                orderItem.getPriceAtOrder());
    }
}
