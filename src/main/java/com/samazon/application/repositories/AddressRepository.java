package com.samazon.application.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.samazon.application.models.Address;

public interface AddressRepository extends JpaRepository<Address, Long>{
    

}
