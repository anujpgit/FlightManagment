package com.anuj.user.service;

import com.anuj.user.dto.RegisterRequest;
import com.anuj.user.dto.RegisterResponse;
import com.anuj.user.entity.Role;
import com.anuj.user.entity.User;
import com.anuj.user.repo.RoleRepository;
import com.anuj.user.repo.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Collections;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public RegisterResponse register(RegisterRequest request) {
        logger.info("Entering register method with request: {}", request);

        // Check if email exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            logger.error("Email already registered: {}", request.getEmail());
            throw new RuntimeException("Email already registered.");
        }

        // Check if username exists
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            logger.error("Username already taken: {}", request.getUsername());
            throw new RuntimeException("Username already taken.");
        }

        // Load default role: ROLE_PASSENGER
        logger.info("Loading default role: ROLE_PASSENGER");
        Role passengerRole = roleRepository.findByName("ROLE_PASSENGER")
                .orElseThrow(() -> {
                    logger.error("ROLE_PASSENGER not found in database");
                    return new RuntimeException("ROLE_PASSENGER not found in database");
                });

        // Encode password
        logger.info("Encoding password for user: {}", request.getUsername());
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        // Create new user
        logger.info("Creating new user with username: {}", request.getUsername());
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(hashedPassword);
        user.setEnabled(true);
        user.setCreatedAt(Instant.now());
        user.setRoles(Collections.singleton(passengerRole));

        // Save user
        logger.info("Saving user to database: {}", user);
        userRepository.save(user);

        logger.info("Exiting register method with success response");
        return new RegisterResponse("User registered successfully.");
    }
}