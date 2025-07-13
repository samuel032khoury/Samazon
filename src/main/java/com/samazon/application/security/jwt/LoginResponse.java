package com.samazon.application.security.jwt;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class LoginResponse {
    private String jwtToken;
    private String username;
    private List<String> roles;
}
