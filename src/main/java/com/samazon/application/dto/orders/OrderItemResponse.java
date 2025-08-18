package com.samazon.application.dto.orders;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemResponse {
    private Long id;
    private Integer quantity;
    private Double unitPriceAtOrder;
    private Double discount;
    private Long productId;
    private String productName;
    private String productImage;
    private Long sellerId;
    private String sellerName;
}