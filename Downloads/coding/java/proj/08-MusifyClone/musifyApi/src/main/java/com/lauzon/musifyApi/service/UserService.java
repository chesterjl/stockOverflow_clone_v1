package com.lauzon.musifyApi.service;

import com.lauzon.musifyApi.dto.request.ArtistRequest;

import java.util.Map;

public interface UserService {

    Map<String, String> requestArtistVerification(ArtistRequest request);
}
