package com.samazon.application.dto.addresses;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressRequest {
    @NotBlank
    private String addressLine1;
    private String addressLine2;
    @NotBlank
    @Size(min = 2, message = "City must be at least 2 characters long")
    private String city;
    @NotBlank
    @Size(min = 2, message = "State must be at least 2 characters long")
    private String state;
    @NotBlank
    private String postalCode;
    @NotBlank
    @Size(min = 2, message = "Country must be at least 2 characters long")
    private String country;
}