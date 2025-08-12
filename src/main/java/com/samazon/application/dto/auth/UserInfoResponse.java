package com.samazon.application.dto.auth;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserInfoResponse {
    private Long id;
    private String username;
    private List<String> roles;
}
