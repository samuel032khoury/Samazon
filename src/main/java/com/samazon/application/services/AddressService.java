package com.samazon.application.services;

import java.util.List;

import com.samazon.application.dto.addresses.AddressRequest;
import com.samazon.application.dto.addresses.AddressResponse;
import com.samazon.application.models.User;

public interface AddressService {

    AddressResponse createAddress(AddressRequest request, User user);

    List<AddressResponse> getAddressesByUser(User user);

    List<AddressResponse> getAllAddresses();

    AddressResponse getAddressById(Long addressId);

    Void deleteAddress(Long addressId, User user);

    AddressResponse updateAddress(Long addressId, AddressRequest request, User user);

}
