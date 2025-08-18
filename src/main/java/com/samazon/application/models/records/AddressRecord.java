package com.samazon.application.models.records;

import com.samazon.application.dto.addresses.AddressRequest;
import com.samazon.application.models.Address;

import jakarta.persistence.Embeddable;

@Embeddable
public record AddressRecord(String addressLine1, String addressLine2, String city, String state,
        String postalCode, String country) {
    public AddressRecord(AddressRequest request) {
        this(request.getAddressLine1(), request.getAddressLine2(), request.getCity(),
                request.getState(), request.getPostalCode(), request.getCountry());
    }

    public AddressRecord(Address address) {
        this(address.getAddressLine1(), address.getAddressLine2(), address.getCity(),
                address.getState(), address.getPostalCode(), address.getCountry());
    }
}
