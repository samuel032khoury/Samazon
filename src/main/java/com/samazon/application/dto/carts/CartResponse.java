package com.samazon.application.dto.carts;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartResponse {
    private Long id;
    private Double totalPrice = 0.0;
    private List<CartItemResponse> cartItems = new ArrayList<>();
}
