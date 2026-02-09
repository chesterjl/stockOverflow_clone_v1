package com.lauzon.musifyApi.controller;

import com.lauzon.musifyApi.dto.response.ArtistResponse;
import com.lauzon.musifyApi.dto.response.UserResponse;
import com.lauzon.musifyApi.service.impl.AdminServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminServiceImpl adminService;

    @GetMapping("/request/artist/{requestId}")
    public ResponseEntity<ArtistResponse> viewRequest(@PathVariable String requestId) {
        ArtistResponse response = adminService.viewPendingArtistRequest(requestId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/request/artist")
    public ResponseEntity<List<ArtistResponse>> viewAllRequest() {
        List<ArtistResponse> response = adminService.viewAllPendingArtistRequest();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/request/artist/approve/{requestId}")
    public ResponseEntity<Map<String, String> > approveRequest(@PathVariable String requestId) {
        Map<String, String> response = adminService.approveArtistRequest(requestId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/request/artist/reject/{requestId}")
    public ResponseEntity<Map<String, String> > rejectRequest(@PathVariable String requestId) {
        Map<String, String> response = adminService.rejectArtistRequest(requestId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<UserResponse> viewUser(@PathVariable String userId) {
        UserResponse user = adminService.viewUser(userId);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> viewAllUsers() {
        List<UserResponse> users = adminService.viewAllUsers();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/users/{userId}")
    public ResponseEntity<Map<String, String>> makeUserAdmin(@PathVariable String userId) {
        Map<String, String> response = adminService.makeUserAdmin(userId);
        return ResponseEntity.ok(response);
    }

}

