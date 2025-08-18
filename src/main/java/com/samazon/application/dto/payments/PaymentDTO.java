package com.samazon.application.dto.payments;

import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {
    private Long id;
    @NotBlank
    private String paymentMethod;
    private String paymentGatewayProvider;
    private String paymentGatewayId;
    private String paymentGatewayStatus;
    private String paymentGatewayResponse;
}