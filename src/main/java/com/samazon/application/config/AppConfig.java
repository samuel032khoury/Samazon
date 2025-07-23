package com.samazon.application.config;

import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.samazon.application.models.Role;
import com.samazon.application.models.RoleType;
import com.samazon.application.repositories.RoleRepository;

@Configuration
public class AppConfig {
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public CommandLineRunner initData(RoleRepository roleRepository) {
        return args -> {
            roleRepository.findByRoleType(RoleType.ROLE_USER)
                    .orElseGet(() -> {
                        Role newUserRole = new Role();
                        newUserRole.setRoleType(RoleType.ROLE_USER);
                        return roleRepository.save(newUserRole);
                    });

            roleRepository.findByRoleType(RoleType.ROLE_SELLER)
                    .orElseGet(() -> {
                        Role newSellerRole = new Role();
                        newSellerRole.setRoleType(RoleType.ROLE_SELLER);
                        return roleRepository.save(newSellerRole);
                    });

            roleRepository.findByRoleType(RoleType.ROLE_ADMIN)
                    .orElseGet(() -> {
                        Role newAdminRole = new Role();
                        newAdminRole.setRoleType(RoleType.ROLE_ADMIN);
                        return roleRepository.save(newAdminRole);
                    });

        };
    }
}
