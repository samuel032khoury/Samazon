package com.samazon.application.services;

import com.samazon.application.dto.auth.LoginRequest;
import com.samazon.application.dto.auth.SignUpRequest;
import com.samazon.application.dto.auth.UserInfoResponse;

public interface AuthService {

    UserInfoResponse authenticate(LoginRequest request);

    UserInfoResponse register(SignUpRequest request);

}
