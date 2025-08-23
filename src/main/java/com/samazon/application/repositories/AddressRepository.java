package com.samazon.application.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.samazon.application.models.Address;
import com.samazon.application.models.User;

public interface AddressRepository extends JpaRepository<Address, Long> {

    Boolean existsByUserIdAndAddressLine1AndAddressLine2AndCityAndStateAndPostalCodeAndCountryAndIdNot(
            Long userId, String addressLine1, String addressLine2, String city, String state,
            String postalCode, String country, Long id);

    List<Address> findByUser(User user);

}
