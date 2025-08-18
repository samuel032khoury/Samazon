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
    @Size(min = 3, message = "Full name must be at least 3 characters long")
    private String fullName;
    @NotBlank
    @Size(min = 5, message = "Address Line 1 must be at least 5 characters long")
    private String addressLine1;
    @NotBlank
    @Size(min = 5, message = "Address Line 2 must be at least 5 characters long")
    private String addressLine2;
    @NotBlank
    @Size(min = 4, message = "City must be at least 4 characters long")
    private String city;
    @NotBlank
    @Size(min = 2, message = "State must be at least 2 characters long")
    private String state;
    @NotBlank
    @Size(min = 5, message = "Postal code must be at least 5 characters long")
    private String postalCode;
    @NotBlank
    @Size(min = 2, message = "Country must be at least 2 characters long")
    private String country;
    @NotBlank
    @Size(min = 10, message = "Phone number must be at least 10 characters long")
    private String phoneNumber;
}