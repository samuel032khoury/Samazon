package com.samazon.application.services;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.samazon.application.dto.auth.LoginRequest;
import com.samazon.application.dto.auth.SignUpRequest;
import com.samazon.application.dto.auth.UserInfoResponse;
import com.samazon.application.exceptions.APIException;
import com.samazon.application.exceptions.UnauthorizedException;
import com.samazon.application.models.Cart;
import com.samazon.application.models.Role;
import com.samazon.application.models.User;
import com.samazon.application.models.enums.RoleType;
import com.samazon.application.repositories.RoleRepository;
import com.samazon.application.repositories.UserRepository;
import com.samazon.application.security.services.CustomUserDetails;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final RoleRepository roleRepository;

    @Override
    public UserInfoResponse authenticate(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();
            return new UserInfoResponse(userDetails.getId(), userDetails.getUsername(), roles);
        } catch (AuthenticationException e) {
            throw new UnauthorizedException("Invalid username or password");
        }
    }

    @Override
    public UserInfoResponse register(SignUpRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new APIException("Error: Username is already taken!");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new APIException("Error: Email is already in use!");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(encoder.encode(request.getPassword()));

        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByRoleType(RoleType.ROLE_USER)
                .orElseThrow(() -> new APIException("Error: Role not found."));
        roles.add(userRole);
        user.setRoles(roles);
        user.setCart(new Cart(user));
        User newUser = userRepository.save(user);
        return new UserInfoResponse(newUser.getId(), newUser.getUsername(), List.of("ROLE_USER"));
    }

}
