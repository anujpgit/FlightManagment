package com.anuj.user.controller;

import com.anuj.user.dto.LoginRequest;
import com.anuj.user.dto.LoginResponse;
import com.anuj.user.dto.RegisterRequest;
import com.anuj.user.dto.RegisterResponse;
import com.anuj.user.entity.User;
import com.anuj.user.repo.UserRepository;
import com.anuj.user.security.JwtUtil;
import com.anuj.user.service.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/users")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    public AuthController(AuthenticationManager authenticationManager,
                          UserRepository userRepository,
                          JwtUtil jwtUtil,
                          UserService userService) {

        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @PostMapping("/register")
    public RegisterResponse register(@RequestBody RegisterRequest request) {
        logger.info("Entering register method with request: {}", request);
        RegisterResponse response = userService.register(request);
        logger.info("Exiting register method with response: {}", response);
        return response;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        logger.info("Entering login method with request: {}", request);

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        UserDetails userDetails = (UserDetails) auth.getPrincipal();

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    logger.error("User not found for email: {}", request.getEmail());
                    return new RuntimeException("User not found");
                });

        // ⭐ UPDATED → Pass userId in JWT
        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getRoles(),
                user.getId()
        );

        LoginResponse response = new LoginResponse(token, "Login successful");
        logger.info("Exiting login method with response: {}", response);
        return response;
    }
}