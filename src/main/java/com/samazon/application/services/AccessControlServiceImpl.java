package com.samazon.application.services;

import org.springframework.stereotype.Service;

import com.samazon.application.exceptions.APIException;
import com.samazon.application.exceptions.ResourceNotFoundException;
import com.samazon.application.models.Role;
import com.samazon.application.models.User;
import com.samazon.application.models.enums.RoleType;
import com.samazon.application.repositories.RoleRepository;
import com.samazon.application.repositories.UserRepository;
import com.samazon.application.utils.AuthUtil;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AccessControlServiceImpl implements AccessControlService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthUtil authUtil;

    @Override
    public void assignAdminRole(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (authUtil.getCurrentUser().getId().equals(user.getId())) {
            throw new APIException("Cannot assign admin role to yourself");
        }

        Role adminRole = roleRepository.findByRoleType(RoleType.ROLE_ADMIN)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "type", RoleType.ROLE_ADMIN.toString()));

        if (user.getRoles().contains(adminRole)) {
            throw new APIException("User already has admin role");
        }

        user.getRoles().add(adminRole);
        userRepository.save(user);
    }

    @Override
    public void revokeAdminRole(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (authUtil.getCurrentUser().getId().equals(user.getId())) {
            throw new APIException("Cannot revoke admin role from yourself");
        }

        Role adminRole = roleRepository.findByRoleType(RoleType.ROLE_ADMIN)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "type", RoleType.ROLE_ADMIN.toString()));

        if (!user.getRoles().contains(adminRole)) {
            throw new APIException("Cannot remove admin role from this user");
        }

        user.getRoles().remove(adminRole);
        userRepository.save(user);
    }

}
