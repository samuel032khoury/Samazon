package com.samazon.application.dto.payments;

import java.time.LocalDateTime;
import java.util.Map;

import com.samazon.application.models.enums.PaymentStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponse {
    private Long id;
    private Long orderId;
    private Double amount;
    private PaymentStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;

    private String providerPaymentId;
    private Map<String, String> providerPayloads;
}