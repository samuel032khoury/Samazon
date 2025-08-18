package com.samazon.application.dto.carts;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemUpdateQuantityRequest {
    @NotNull
    @Min(value = 1, message = "Please provide a valid quantity")
    Integer quantity;
    @NotNull
    @Pattern(regexp = "^(increment|decrement|set)$", message = "Action must be 'increment', 'decrement', or 'set'")
    String action;
}
