package com.samazon.application.services;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.samazon.application.models.Cart;
import com.samazon.application.models.Role;
import com.samazon.application.models.RoleType;
import com.samazon.application.models.User;
import com.samazon.application.repositories.CartRepository;
import com.samazon.application.repositories.RoleRepository;
import com.samazon.application.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DataSeedService implements CommandLineRunner {

    @Value("${samazon.app.super.password}")
    private String SUPER_CUSTOM_PASSWORD;

    @Value("${samazon.app.super.email}")
    private String SUPER_EMAIL;

    @Value("${samazon.app.super.username}")
    private String SUPER_USERNAME;

    private final CartRepository cartRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private static final Logger logger = LoggerFactory.getLogger(DataSeedService.class);

    @Transactional
    public void seedData() {
        logger.info("Starting data seeding process...");

        // Seed roles first
        seedRoles();

        // Then seed super admin
        seedSuperAdmin();

        logger.info("Data seeding process completed successfully");
    }

    private void seedRoles() {
        logger.debug("Seeding roles...");

        // Create all role types
        for (RoleType roleType : RoleType.values()) {
            roleRepository.findByRoleType(roleType)
                    .orElseGet(() -> {
                        Role newRole = new Role();
                        newRole.setRoleType(roleType);
                        Role savedRole = roleRepository.save(newRole);
                        logger.debug("Created role: {}", roleType);
                        return savedRole;
                    });
        }

        logger.info("All roles have been seeded successfully");
    }

    private void seedSuperAdmin() {
        // Check if super admin already exists
        if (userRepository.findByUsername(SUPER_USERNAME).isPresent()) {
            logger.info("Super admin already exists. Skipping super admin creation.");
            return;
        }

        logger.info("Creating super admin user...");

        // Generate a secure random password if not provided
        String password = Optional.ofNullable(SUPER_CUSTOM_PASSWORD)
                .filter(p -> !p.isEmpty())
                .orElseGet(this::generateSecurePassword);

        // Get super admin role
        Role superAdminRole = roleRepository.findByRoleType(RoleType.ROLE_SUPER_ADMIN)
                .orElseThrow(() -> new RuntimeException("ROLE_SUPER_ADMIN not found. Ensure roles are seeded first."));

        // Create super admin user
        User superAdmin = new User();
        superAdmin.setUsername(SUPER_USERNAME);
        superAdmin.setEmail(SUPER_EMAIL);
        superAdmin.setPassword(passwordEncoder.encode(password));

        Set<Role> roles = new HashSet<>();
        roles.add(superAdminRole);
        superAdmin.setRoles(roles);

        userRepository.save(superAdmin);

        Cart superAdminCart = new Cart();
        superAdminCart.setUser(superAdmin);
        cartRepository.save(superAdminCart);

        // Log credentials ONLY on first creation
        logger.warn("========================================");
        logger.warn("SUPER ADMIN CREDENTIALS (SAVE THESE!)");
        logger.warn("Username: {}", SUPER_USERNAME);
        logger.warn("Password: {}", password);
        logger.warn("Email: {}", SUPER_EMAIL);
        logger.warn("========================================");
        logger.warn("IMPORTANT: These credentials will NOT be shown again!");
        logger.warn("========================================");

        logger.info("Super admin user created successfully");
    }

    private String generateSecurePassword() {
        final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
        final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        final String DIGITS = "0123456789";
        final String SPECIAL_CHARS = "!@#$%^&*()_+-=[]{}|;:,.<>?";
        final String ALL_CHARS = LOWERCASE + UPPERCASE + DIGITS + SPECIAL_CHARS;

        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        // Ensure password contains at least one character from each category
        password.append(LOWERCASE.charAt(random.nextInt(LOWERCASE.length())));
        password.append(UPPERCASE.charAt(random.nextInt(UPPERCASE.length())));
        password.append(DIGITS.charAt(random.nextInt(DIGITS.length())));
        password.append(SPECIAL_CHARS.charAt(random.nextInt(SPECIAL_CHARS.length())));

        // Fill the rest with random characters (minimum 12 characters total)
        for (int i = 4; i < 16; i++) {
            password.append(ALL_CHARS.charAt(random.nextInt(ALL_CHARS.length())));
        }

        // Shuffle the password
        return shuffleString(password.toString(), random);
    }

    private String shuffleString(String input, SecureRandom random) {
        char[] characters = input.toCharArray();
        for (int i = characters.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = characters[i];
            characters[i] = characters[j];
            characters[j] = temp;
        }
        return new String(characters);
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        this.seedData();
    }
}