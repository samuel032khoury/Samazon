package com.samazon.application.services;

import java.util.List;

import com.samazon.application.dto.addresses.AddressRequest;
import com.samazon.application.dto.addresses.AddressResponse;
import com.samazon.application.models.User;

public interface AddressService {

    AddressResponse createAddress(AddressRequest request, User user);

    List<AddressResponse> getAllAddresses();

    List<AddressResponse> getAddressesByUser(User user);

    AddressResponse getAddressById(Long addressId);

    AddressResponse updateAddress(Long addressId, AddressRequest request, User user);

    Void deleteAddress(Long addressId, User user);

}
