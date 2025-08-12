package com.samazon.application.dto.addresses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponse {
    private Long id;
    private String building;
    private String street;
    private String city;
    private String state;
    private String country;
    private String zipCode;
}