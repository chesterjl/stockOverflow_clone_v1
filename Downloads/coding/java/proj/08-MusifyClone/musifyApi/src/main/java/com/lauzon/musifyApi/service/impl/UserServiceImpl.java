package com.lauzon.musifyApi.service.impl;

import com.lauzon.musifyApi.document.ArtistDocument;
import com.lauzon.musifyApi.document.UserDocument;
import com.lauzon.musifyApi.dto.request.ArtistRequest;
import com.lauzon.musifyApi.enums.ArtistStatus;
import com.lauzon.musifyApi.repository.ArtistRepository;
import com.lauzon.musifyApi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final AuthServiceImpl authService;
    private final ArtistRepository artistRepository;

    @Override
    public Map<String, String> requestArtistVerification(ArtistRequest request) {
        UserDocument user = authService.getCurrentUser();

        boolean alreadyRequested = artistRepository.existsByUserIdAndStatus(user.getId(), ArtistStatus.PENDING);
        if (alreadyRequested) {
            throw new IllegalStateException("You already have a pending artist request");
        }

        if (user.getRole().name().equals("ARTIST")) {
            throw new IllegalArgumentException("You already an artist");
        }

        if (user.getRole().name().equals("ADMIN")) {
            throw new AccessDeniedException("You can't be an artist");
        }

        ArtistDocument requestArtist = convertToDocument(request, user);
        artistRepository.save(requestArtist);
        return Map.of("message", "Your request to become an artist is now pending approval");
    }

    private ArtistDocument convertToDocument(ArtistRequest request, UserDocument userDocument) {
        return ArtistDocument.builder()
                .userId(userDocument.getId())
                .stageName(request.getStageName())
                .proofUrl(request.getProofUrl())
                .message(request.getMessage() != null ? request.getMessage() : "")
                .status(ArtistStatus.PENDING)
                .build();
    }
}
