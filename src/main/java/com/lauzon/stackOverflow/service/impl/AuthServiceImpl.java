package com.lauzon.stackOverflow.service.impl;

import com.lauzon.stackOverflow.dto.request.LoginRequest;
import com.lauzon.stackOverflow.dto.request.RegisterUserRequest;
import com.lauzon.stackOverflow.dto.response.UserResponse;
import com.lauzon.stackOverflow.entity.UserEntity;
import com.lauzon.stackOverflow.enums.Role;
import com.lauzon.stackOverflow.repository.UserRepository;
import com.lauzon.stackOverflow.service.AuthService;
import com.lauzon.stackOverflow.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final EmailServiceImpl emailService;

    @Value("${solveit_backend_url}")
    private String ACTIVATION_URL;

    @Override
    public UserResponse register(RegisterUserRequest request) {
        UserEntity newUser = convertToEntity(request);
        newUser.setActivationToken(UUID.randomUUID().toString());
        newUser = userRepository.save(newUser);
        String activationLink = ACTIVATION_URL+"/api/v1/activate?token=" + newUser.getActivationToken();
        String subject = "Activate your SolveIt Account";
        String body = "Click the link below to activate your account:\n" + activationLink;
        emailService.sendEmail(newUser.getEmail(), subject, body);
        return convertToResponseDto(newUser);
    }

    @Override
    public Map<String, Object> login(LoginRequest loginRequest) {
        if (!isAccountActive(loginRequest.getEmail())) {
            return Map.of("message", "Account is not active. Please activate your account first");
        }

       return authenticateAndGenerateToken(loginRequest);
    }

    @Override
    public boolean activateAccount(String activationToken) {
        return userRepository.findByActivationToken(activationToken)
                .map(user -> {
                    user.setIsActive(true);
                    userRepository.save(user);
                    return true;
                })
                .orElse(false);
    }

    private boolean isAccountActive(String email) {
        return userRepository.findByEmail(email)
                .map(UserEntity::getIsActive)
                .orElse(false);
    }

    public UserEntity getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + authentication.getName()));
    }

    public UserResponse getPublicProfile(String email) {
        UserEntity user = null;
        if (email == null) {
            user = getCurrentUser();
        } else {
            user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        }

        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    private Map<String, Object> authenticateAndGenerateToken(LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

            UserEntity user = userRepository.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + loginRequest.getEmail()));

            String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

            return Map.of(
                    "token", token,
                    "user", getPublicProfile(user.getEmail())
            );
        } catch (Exception e) {
            throw new BadCredentialsException("Invalid email or password.");
        }
    }

    private UserEntity convertToEntity(RegisterUserRequest request) {
        return UserEntity.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole() != null ? Role.valueOf(request.getRole()) : Role.USER)
                .build();
    }

    private UserResponse convertToResponseDto(UserEntity user) {
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole() != null ? user.getRole().name() : null)
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
