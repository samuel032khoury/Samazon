package com.samazon.application.controllers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.samazon.application.dto.UserInfoDTO;
import com.samazon.application.dto.requests.LoginRequestDTO;
import com.samazon.application.dto.requests.SingUpRequestDTO;
import com.samazon.application.models.Role;
import com.samazon.application.models.RoleType;
import com.samazon.application.models.User;
import com.samazon.application.repositories.RoleRepository;
import com.samazon.application.repositories.UserRepository;
import com.samazon.application.responses.APIResponse;
import com.samazon.application.security.jwt.JwtUtils;
import com.samazon.application.services.UserDetailsImpl;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final RoleRepository roleRepository;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequestDTO loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            ResponseCookie jwtCookie = jwtUtils.generateJwtCookieForUser(userDetails);
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());
            return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                    .body(new UserInfoDTO(userDetails.getId(), userDetails.getUsername(), roles));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
                    .body(new APIResponse("Invalid username or password", false));
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SingUpRequestDTO signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new APIResponse("Error: Username is already taken!", false));
        }
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new APIResponse("Error: Email is already in use!", false));
        }

        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(encoder.encode(signUpRequest.getPassword()));

        Set<String> strRoles = signUpRequest.getRoles();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null || strRoles.isEmpty()) {
            Role userRole = roleRepository.findByRoleType(RoleType.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByRoleType(RoleType.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role not found."));
                        roles.add(adminRole);
                        break;
                    case "seller":
                        Role sellerRole = roleRepository.findByRoleType(RoleType.ROLE_SELLER)
                                .orElseThrow(() -> new RuntimeException("Error: Role not found."));
                        roles.add(sellerRole);
                        break;
                    default:
                        Role userRole = roleRepository.findByRoleType(RoleType.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role not found."));
                        roles.add(userRole);
                }
            });
        }
        user.setRoles(roles);
        userRepository.save(user);
        return ResponseEntity.ok(new APIResponse("User registered successfully!", true));
    }

    @PostMapping("/signout")
    public ResponseEntity<?> logoutUser() {
        ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new APIResponse("You've been signed out!", true));
    }

    @GetMapping("/username")
    public String currentUserName(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        return authentication.getName();
    }
}
