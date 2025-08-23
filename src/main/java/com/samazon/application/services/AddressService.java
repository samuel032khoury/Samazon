package com.samazon.application.services;

import java.util.List;

import com.samazon.application.dto.addresses.AddressRequest;
import com.samazon.application.dto.addresses.AddressResponse;
import com.samazon.application.models.User;

import jakarta.transaction.Transactional;

public interface AddressService {

    @Transactional
    AddressResponse createAddress(User user, AddressRequest request);

    List<AddressResponse> getAllAddresses();

    List<AddressResponse> getAddressesByUser(User user);

    AddressResponse getAddressById(Long addressId);

    AddressResponse updateAddress(Long addressId, AddressRequest request);

    Void deleteAddress(Long addressId);

    Void checkPermission(User user, Long addressId);
}
