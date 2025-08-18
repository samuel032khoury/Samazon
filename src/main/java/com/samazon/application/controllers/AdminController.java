package com.samazon.application.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.samazon.application.dto.common.APIResponse;
import com.samazon.application.services.AccessControlService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@AllArgsConstructor
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class AdminController {

    private final AccessControlService accessControlService;

    @PostMapping("/assign-admin/{userId}")
    public ResponseEntity<APIResponse> assignAdminRole(@PathVariable Long userId) {
        accessControlService.assignAdminRole(userId);
        return ResponseEntity.ok(new APIResponse("Admin role assigned successfully", true));
    }

    @PostMapping("/revoke-admin/{userId}")
    public ResponseEntity<APIResponse> revokeAdminRole(@PathVariable Long userId) {
        accessControlService.revokeAdminRole(userId);
        return ResponseEntity.ok(new APIResponse("Admin role revoked successfully", true));
    }

}
