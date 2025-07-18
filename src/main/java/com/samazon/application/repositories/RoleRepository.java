package com.samazon.application.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.samazon.application.models.Role;
import com.samazon.application.models.RoleType;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    boolean existsByRoleType(String name);

    Optional<Role> findByRoleType(RoleType roleType);
}
