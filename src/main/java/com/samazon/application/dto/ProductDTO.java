package com.samazon.application.dto;

import com.samazon.validation.OnCreateOrUpdate;
import com.samazon.validation.OnPatch;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private Long id;

    @NotBlank(groups = OnCreateOrUpdate.class)
    private String name;

    private String description;

    @Size(min = 1, message = "must not be empty", groups = { OnCreateOrUpdate.class, OnPatch.class })
    private String image;

    @PositiveOrZero(groups = { OnCreateOrUpdate.class, OnPatch.class })
    private Integer quantity;

    @NotNull(groups = OnCreateOrUpdate.class)
    @PositiveOrZero(groups = { OnCreateOrUpdate.class, OnPatch.class })
    private Double price;

    @PositiveOrZero(groups = { OnCreateOrUpdate.class, OnPatch.class })
    @Max(value = 100, groups = { OnCreateOrUpdate.class, OnPatch.class })
    private Double discount;

    private Double specialPrice;
}