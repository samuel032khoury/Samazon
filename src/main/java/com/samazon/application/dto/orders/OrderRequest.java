package com.samazon.application.dto.orders;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
    private Long addressId;
    private String paymentMethod;
    private String paymentGatewayProvider;
    private String paymentGatewayId;
    private String paymentGatewayStatus;
    private String paymentGatewayResponse;
}
