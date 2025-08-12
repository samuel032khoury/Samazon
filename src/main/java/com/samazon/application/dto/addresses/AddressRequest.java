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
    @Size(min = 5, message = "Building must be at least 5 characters long")
    private String building;
    @NotBlank
    @Size(min = 5, message = "Street must be at least 5 characters long")
    private String street;
    @NotBlank
    @Size(min = 4, message = "City must be at least 4 characters long")
    private String city;
    @NotBlank
    @Size(min = 2, message = "State must be at least 2 characters long")
    private String state;
    @NotBlank
    @Size(min = 2, message = "Country must be at least 2 characters long")
    private String country;
    @NotBlank
    @Size(min = 5, message = "Zip code must be at least 5 characters long")
    private String zipCode;
}