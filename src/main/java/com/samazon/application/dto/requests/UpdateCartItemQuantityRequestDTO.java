package com.samazon.application.dto.requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCartItemQuantityRequestDTO {
    @NotNull
    Long productId;
    @NotNull
    @Min(value = 1, message = "Please provide a valid quantity")
    Integer quantity;
    @NotNull
    @Pattern(regexp = "^(increment|decrement|set)$", message = "Action must be 'increment', 'decrement', or 'set'")
    String action;
}
