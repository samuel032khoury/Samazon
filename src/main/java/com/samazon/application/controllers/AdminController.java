package com.samazon.application.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.samazon.application.dto.common.APIResponse;
import com.samazon.application.exceptions.ResourceNotFoundException;
import com.samazon.application.models.Role;
import com.samazon.application.models.User;
import com.samazon.application.models.enums.RoleType;
import com.samazon.application.repositories.RoleRepository;
import com.samazon.application.repositories.UserRepository;
import com.samazon.application.utils.AuthUtil;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@AllArgsConstructor
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class AdminController {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthUtil authUtil;

    @PostMapping("/assign-admin/{userId}")
    public ResponseEntity<APIResponse> assignAdminRole(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (authUtil.getCurrentUser().getId().equals(user.getId())) {
            return ResponseEntity.badRequest().body(new APIResponse("Cannot assign admin role to yourself", false));
        }

        Role adminRole = roleRepository.findByRoleType(RoleType.ROLE_ADMIN)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "type", RoleType.ROLE_ADMIN.toString()));

        if (user.getRoles().contains(adminRole)) {
            return ResponseEntity.badRequest().body(new APIResponse("User already has admin role", false));
        }

        user.getRoles().add(adminRole);
        userRepository.save(user);

        return ResponseEntity.ok(new APIResponse("Admin role assigned successfully", true));
    }

    @PostMapping("/revoke-admin/{userId}")
    public ResponseEntity<APIResponse> revokeAdminRole(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (authUtil.getCurrentUser().getId().equals(user.getId())) {
            return ResponseEntity.badRequest().body(new APIResponse("Cannot revoke admin role from yourself", false));
        }

        Role adminRole = roleRepository.findByRoleType(RoleType.ROLE_ADMIN)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "type", RoleType.ROLE_ADMIN.toString()));

        if (!user.getRoles().contains(adminRole)) {
            return ResponseEntity.badRequest().body(new APIResponse("Cannot remove admin role from this user", false));
        }

        user.getRoles().remove(adminRole);
        userRepository.save(user);

        return ResponseEntity.ok(new APIResponse("Admin role revoked successfully", true));
    }
}
