package com.lauzon.musifyApi.service;

import com.lauzon.musifyApi.dto.request.AlbumRequest;
import com.lauzon.musifyApi.dto.response.AlbumResponse;

import java.io.IOException;
import java.util.List;

public interface AlbumService {

    AlbumResponse addAlbum(AlbumRequest request) throws IOException;

    AlbumResponse updateAlbum(AlbumRequest request, String albumId) throws IOException;

    List<AlbumResponse> getAllAlbumsForArtist();

    void removeAlbum(String albumId);

    List<AlbumResponse> getAllAlbums();

    List<AlbumResponse> searchAlbums(String keyword);

}
