package com.samazon.application.dto.orders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.samazon.application.dto.payments.PaymentResponse;
import com.samazon.application.models.enums.OrderStatus;
import com.samazon.application.models.records.AddressRecord;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Long id;
    private LocalDate orderDate;
    private String fullName;
    private String email;
    private String phoneNumber;
    private BigDecimal totalAmount;
    private OrderStatus orderStatus;
    private AddressRecord shippingAddress;
    private AddressRecord billingAddress;
    private List<OrderItemResponse> orderItems;
    private PaymentResponse payment;
}