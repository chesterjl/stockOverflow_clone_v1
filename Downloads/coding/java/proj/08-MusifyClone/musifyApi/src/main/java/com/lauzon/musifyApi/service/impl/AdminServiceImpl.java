package com.lauzon.musifyApi.service.impl;

import com.lauzon.musifyApi.document.ArtistDocument;
import com.lauzon.musifyApi.document.UserDocument;
import com.lauzon.musifyApi.dto.response.ArtistResponse;
import com.lauzon.musifyApi.dto.response.UserResponse;
import com.lauzon.musifyApi.enums.ArtistStatus;
import com.lauzon.musifyApi.enums.Role;
import com.lauzon.musifyApi.exceptions.ResourceNotFoundException;
import com.lauzon.musifyApi.exceptions.UserNotFoundException;
import com.lauzon.musifyApi.repository.ArtistRepository;
import com.lauzon.musifyApi.repository.UserRepository;
import com.lauzon.musifyApi.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final ArtistRepository artistRepository;
    private final UserRepository userRepository;

    @Override
    public ArtistResponse viewPendingArtistRequest(String requestId) {
        ArtistDocument artistDocument = artistRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request id not found"));
        return convertToResponse(artistDocument);
    }

    @Override
    public List<ArtistResponse> viewAllPendingArtistRequest() {
        List<ArtistDocument> artistDocuments = artistRepository.findAll();
        return artistDocuments.stream().map(this::convertToResponse).toList();
    }

    @Override
    public Map<String, String> approveArtistRequest(String requestId) {
        ArtistDocument artistDocument = artistRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request id not found"));
        artistDocument.setStatus(ArtistStatus.VERIFIED);
        artistRepository.save(artistDocument);

        String userId = artistDocument.getUserId();
        UserDocument user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        user.setRole(Role.ARTIST);
        userRepository.save(user);
        return Map.of("message", "Verified request artist successfully");
    }

    @Override
    public  Map<String, String> rejectArtistRequest(String requestId) {
        ArtistDocument artistDocument = artistRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));
        artistDocument.setStatus(ArtistStatus.REJECTED);
        artistRepository.save(artistDocument);
        return Map.of("message", "Rejected request artist successfully");
    }

    @Override
    public List<UserResponse> viewAllUsers() {
        List<UserDocument> users = userRepository.findAll();
        return users.stream().map(this::convertToUserResponse).toList();
    }

    @Override
    public UserResponse viewUser(String userId) {
        UserDocument user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return convertToUserResponse(user);
    }

    @Override
    public Map<String, String> makeUserAdmin(String userId) {
        UserDocument user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        user.setRole(Role.ADMIN);
        userRepository.save(user);
        return Map.of("message", "Successfully saved user to be admin");
    }

    private ArtistResponse convertToResponse(ArtistDocument artistDocument) {
        return ArtistResponse.builder()
                .id(artistDocument.getId())
                .userId(artistDocument.getUserId())
                .stageName(artistDocument.getStageName())
                .proofUrl(artistDocument.getProofUrl())
                .message(artistDocument.getMessage())
                .status(artistDocument.getStatus())
                .createdAt(artistDocument.getCreatedAt())
                .updatedAt(artistDocument.getUpdatedAt())
                .build();
    }

    private UserResponse convertToUserResponse(UserDocument userDocument) {
        return UserResponse.builder()
                .id(userDocument.getId())
                .username(userDocument.getUsername())
                .email(userDocument.getEmail())
                .role(userDocument.getRole().name())
                .createdAt(userDocument.getCreatedAt())
                .build();
    }

}
