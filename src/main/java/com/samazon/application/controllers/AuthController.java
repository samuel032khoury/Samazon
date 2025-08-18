package com.samazon.application.controllers;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.samazon.application.dto.auth.LoginRequest;
import com.samazon.application.dto.auth.SignUpRequest;
import com.samazon.application.dto.auth.UserInfoResponse;
import com.samazon.application.dto.common.APIResponse;
import com.samazon.application.security.jwt.JwtUtils;
import com.samazon.application.services.AuthService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;

    private final JwtUtils jwtUtils;

    @PostMapping("/signin")
    public ResponseEntity<UserInfoResponse> authenticate(@Valid @RequestBody LoginRequest request) {
        UserInfoResponse response = authService.authenticate(request);
        ResponseCookie jwtCookie = jwtUtils.generateJwtCookieForUser(response.getUsername());
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString()).body(response);
    }

    @PostMapping("/signup")
    public ResponseEntity<UserInfoResponse> register(@Valid @RequestBody SignUpRequest request) {
        UserInfoResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/signout")
    public ResponseEntity<APIResponse> logoutUser() {
        ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new APIResponse("You've been signed out!", true));
    }

    @GetMapping("/username")
    public String currentUserName(Authentication authentication) {
        return authentication.getName();
    }
}