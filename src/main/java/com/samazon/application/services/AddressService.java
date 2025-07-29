package com.samazon.application.services;

import java.util.List;

import com.samazon.application.dto.AddressDTO;
import com.samazon.application.models.User;

public interface AddressService {

    AddressDTO createAddress(AddressDTO addressDTO, User user);

    List<AddressDTO> getAddressesByUser(User user);

    List<AddressDTO> getAllAddresses();

    AddressDTO getAddressById(Long addressId);

}
