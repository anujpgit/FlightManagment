package com.anuj.user.service;

import com.anuj.user.dto.RegisterRequest;
import com.anuj.user.dto.RegisterResponse;

public interface UserService {
    RegisterResponse register(RegisterRequest request);
}