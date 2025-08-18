package com.samazon.application.dto.carts;

import java.math.BigDecimal;
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
    private BigDecimal totalAmount = BigDecimal.ZERO;
    private List<CartItemResponse> cartItems = new ArrayList<>();
}
