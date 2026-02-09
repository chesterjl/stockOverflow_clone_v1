package com.lauzon.musifyApi.controller;

import com.lauzon.musifyApi.dto.request.ArtistRequest;
import com.lauzon.musifyApi.service.impl.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserServiceImpl userService;

    @PostMapping("/request/artist")
    public ResponseEntity<Map<String, String>> requestArtistVerification(@Valid @RequestBody ArtistRequest request) {
        Map<String, String> response = userService.requestArtistVerification(request);
        return ResponseEntity.ok(response);
    }

}
