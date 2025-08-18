package com.samazon.application.services;

import com.samazon.application.dto.auth.LoginRequest;
import com.samazon.application.dto.auth.SignUpRequest;
import com.samazon.application.dto.auth.UserInfoResponse;

import jakarta.transaction.Transactional;

public interface AuthService {

    UserInfoResponse authenticate(LoginRequest request);

    @Transactional
    UserInfoResponse register(SignUpRequest request);

}
