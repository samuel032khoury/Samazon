package com.samazon.application.dto.requests;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddCartItemRequestDTO {
    @NotNull
    Long productId;
    @NotNull
    @Min(value = 1, message = "Quantity must be at least 1")
    Integer quantity;
}
