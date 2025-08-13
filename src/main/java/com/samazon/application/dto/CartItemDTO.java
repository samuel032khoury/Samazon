package com.samazon.application.dto;

import com.samazon.application.dto.products.ProductResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemDTO {
    private Long id;
    private Long cartId;
    private ProductResponse product;
    private Integer quantity;
    private Double discount;
    private Double priceAtAddToCart;
}
