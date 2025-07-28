package com.samazon.application.services;

import com.samazon.application.dto.AddressDTO;
import com.samazon.application.models.User;

public interface AddressService {

    AddressDTO createAddress(AddressDTO addressDTO, User user);

}
