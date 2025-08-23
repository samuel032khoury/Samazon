package com.samazon.application.dto.carts;

import com.samazon.application.dto.products.ProductResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemResponse {
    private Long id;
    private Integer quantity;
    private Double unitPriceAtAddToCart;
    private Long cartId;
    private ProductResponse product;
}