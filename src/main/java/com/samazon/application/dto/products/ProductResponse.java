package com.samazon.application.dto.products;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private String name;
    private String categoryName;
    private String description;
    private String image;
    private Integer stock;
    private Double price;
    private Double discount;
    private Double specialPrice;
    private String sellerUserName;
}