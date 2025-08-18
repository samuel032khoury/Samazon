package com.samazon.application.services;

public interface AccessControlService {

    void assignAdminRole(Long userId);

    void revokeAdminRole(Long userId);

}
