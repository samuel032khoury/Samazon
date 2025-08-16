package com.samazon.application.security;

import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityEndpointsConfig {

    public static final String[] PUBLIC_ENDPOINTS = {
            "/error",
            "/api/auth/**",
            "/api/public/**",
            "/api/test/**",
            "/h2-console/**",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/docs/**",
            "/media/**"
    };

    public static final String[] SWAGGER_ENDPOINTS = {
            "/v2/api-docs",
            "/swagger-resources/**",
            "/swagger-ui.html",
            "/webjars/**",
            "/configuration/ui",
            "/configuration/security"
    };

}
