package com.samazon.application.services;

public interface AccessControlService {

    Void assignAdminRole(Long userId);

    Void revokeAdminRole(Long userId);

}
