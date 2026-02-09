package com.lauzon.musifyApi.service.impl;

import com.lauzon.musifyApi.document.ArtistDocument;
import com.lauzon.musifyApi.document.UserDocument;
import com.lauzon.musifyApi.repository.ArtistRepository;
import com.lauzon.musifyApi.service.ArtistService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArtistServiceImpl implements ArtistService {

    private final AuthServiceImpl authService;
    private final ArtistRepository artistRepository;

    @Override
    public ArtistDocument getCurrentArtist() {
        UserDocument user = authService.getCurrentUser();
        return artistRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BadCredentialsException("Artist not found or you are not an artist"));
    }
}
