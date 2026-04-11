package com.anuj.user.service;

import com.anuj.user.dto.RegisterRequest;
import com.anuj.user.dto.RegisterResponse;
import com.anuj.user.entity.Role;
import com.anuj.user.entity.User;
import com.anuj.user.repo.RoleRepository;
import com.anuj.user.repo.UserRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void testRegisterSuccess() {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("test@example.com");
        req.setUsername("anuj");
        req.setPassword("12345");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(userRepository.findByUsername("anuj")).thenReturn(Optional.empty());
        when(roleRepository.findByName("ROLE_PASSENGER"))
                .thenReturn(Optional.of(new Role(1L, "ROLE_PASSENGER")));
        when(passwordEncoder.encode("12345")).thenReturn("hashed_pw");
        when(userRepository.save(any(User.class))).thenReturn(new User());

        RegisterResponse res = userService.register(req);

        assertEquals("User registered successfully.", res.getMessage());
    }

    @Test
    void testRegisterEmailExists() {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("test@example.com");

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(new User()));

        RuntimeException ex =
                assertThrows(RuntimeException.class, () -> userService.register(req));

        assertEquals("Email already registered.", ex.getMessage());
    }
}