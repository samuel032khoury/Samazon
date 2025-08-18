package com.samazon.application.dto.orders;

import com.samazon.application.dto.addresses.AddressRequest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
    @NotBlank
    private String fullName;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String phoneNumber;

    @Valid
    @NotNull
    private AddressRequest shippingAddress;

    @Valid
    private AddressRequest billingAddress;

    private String couponCode;
    private String specialInstructions;
}
