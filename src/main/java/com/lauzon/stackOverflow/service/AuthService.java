package com.lauzon.stackOverflow.service;

import com.lauzon.stackOverflow.dto.request.LoginRequest;
import com.lauzon.stackOverflow.dto.request.RegisterUserRequest;
import com.lauzon.stackOverflow.dto.response.UserResponse;

import java.util.Map;

public interface AuthService {

    UserResponse register(RegisterUserRequest request);

    Map<String, Object> login(LoginRequest loginRequest);

    boolean activateAccount(String activationToken);
}
