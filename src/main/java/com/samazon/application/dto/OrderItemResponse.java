package com.samazon.application.dto;

import com.samazon.application.models.Product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponse {
    private Long id;
    private Long productId;
    private Product product;
    private Integer quantity;
    private Double discount;
    private Double priceAtOrder;
}
