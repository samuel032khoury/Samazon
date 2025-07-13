package com.samazon.application.repositories;



import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.samazon.application.models.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

}
