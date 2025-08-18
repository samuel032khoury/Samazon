package com.samazon.application.dto.orders;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
    @Valid
    @NotNull
    private AddressRequest shippingAddress;

    @Valid
    private AddressRequest billingAddress;
    private String paymentMethod;
    private String paymentGatewayProvider;
    private String paymentGatewayId;
    private String paymentGatewayStatus;
    private String paymentGatewayResponse;
}
