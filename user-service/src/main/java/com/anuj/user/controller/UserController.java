package com.anuj.user.controller;

import com.anuj.user.entity.User;
import com.anuj.user.repo.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 🔹 USER PROFILE ENDPOINT (Protected)
    @GetMapping("/me")
    public User me(Authentication auth) {
        String email = auth.getName();   // extracted from JWT
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}