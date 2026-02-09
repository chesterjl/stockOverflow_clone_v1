package com.lauzon.musifyApi.service;

import com.lauzon.musifyApi.dto.request.LoginRequest;
import com.lauzon.musifyApi.dto.request.RegisterUserRequest;
import com.lauzon.musifyApi.dto.response.UserResponse;

import java.util.Map;

public interface AuthService {

    Map<String, Object> login(LoginRequest request);

    UserResponse register(RegisterUserRequest request);
}
