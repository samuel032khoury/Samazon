package com.samazon.application.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.samazon.application.models.Address;
import com.samazon.application.models.User;

public interface AddressRepository extends JpaRepository<Address, Long> {
    public List<Address> findByUser(User user);

    public boolean existsByUserIdAndBuildingAndStreetAndCityAndStateAndCountryAndZipCodeAndIdNot(Long userId,
            String building,
            String street, String city, String state, String country, String zipCode, Long id);
}
