package com.lauzon.stackOverflow.controller;

import com.lauzon.stackOverflow.dto.request.LoginRequest;
import com.lauzon.stackOverflow.dto.request.RegisterUserRequest;
import com.lauzon.stackOverflow.dto.response.UserResponse;
import com.lauzon.stackOverflow.service.AuthService;
import com.lauzon.stackOverflow.service.impl.AuthServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthServiceImpl authService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginRequest loginRequest) {
        Map<String, Object> result = authService.login(loginRequest);
        if (result.containsKey("message")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(result);
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterUserRequest request) {
        UserResponse registeredUser = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
    }

    @GetMapping("/activate")
    public ResponseEntity<String> activateAccount(@RequestParam String token) {
        boolean isActivated = authService.activateAccount(token);
        if (isActivated) {
            return ResponseEntity.ok("Account is now activated. You can now login your account in SolveIt.");
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Activation token is not found or already used");
        }
    }
}
