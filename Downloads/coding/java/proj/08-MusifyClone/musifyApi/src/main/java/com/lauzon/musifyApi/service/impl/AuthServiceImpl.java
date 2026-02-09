package com.lauzon.musifyApi.service.impl;

import com.lauzon.musifyApi.document.UserDocument;
import com.lauzon.musifyApi.dto.request.LoginRequest;
import com.lauzon.musifyApi.dto.request.RegisterUserRequest;
import com.lauzon.musifyApi.dto.response.UserResponse;
import com.lauzon.musifyApi.enums.Role;
import com.lauzon.musifyApi.exceptions.UserNotFoundException;
import com.lauzon.musifyApi.repository.UserRepository;
import com.lauzon.musifyApi.service.AuthService;
import com.lauzon.musifyApi.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Override
    public Map<String, Object> login(LoginRequest request) {
        return authenticateAndGenerateToken(request);
    }

    @Override
    public UserResponse register(RegisterUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exist");
        }

        UserDocument newUser = convertToDocument(request);
        newUser = userRepository.save(newUser);
        return convertToResponse(newUser);
    }

    public UserDocument getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    public UserResponse getPublicProfile(String email) {
        UserDocument user = null;

        if (email == null) {
            user = getCurrentUser();
        } else {
            user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UserNotFoundException("User not found"));
        }

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .createdAt(user.getCreatedAt())
                .build();
    }

    private Map<String, Object> authenticateAndGenerateToken(LoginRequest request) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
            UserDocument user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new BadCredentialsException("Email is incorrect"));
            String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
            return Map.of(
                    "token", token,
                    "user", getPublicProfile(user.getEmail())
            );

        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Email or Password is incorrect");
        }
    }



    private UserDocument convertToDocument(RegisterUserRequest request) {
        return UserDocument.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole() != null ? request.getRole() : Role.USER)
                .build();
    }

    private UserResponse convertToResponse(UserDocument userDocument) {
        return UserResponse.builder()
                .id(userDocument.getId())
                .username(userDocument.getUsername())
                .email(userDocument.getEmail())
                .role(userDocument.getRole().name())
                .createdAt(userDocument.getCreatedAt())
                .build();
    }

}
