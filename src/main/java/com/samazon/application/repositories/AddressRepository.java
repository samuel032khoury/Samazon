package com.samazon.application.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.samazon.application.models.Address;
import com.samazon.application.models.User;

public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByUser(User user);
}
