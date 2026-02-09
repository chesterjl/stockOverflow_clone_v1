package com.lauzon.musifyApi.controller;

import com.lauzon.musifyApi.dto.response.AlbumResponse;
import com.lauzon.musifyApi.dto.response.SongResponse;
import com.lauzon.musifyApi.service.impl.AlbumServiceImpl;
import com.lauzon.musifyApi.service.impl.SongServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/albums")
public class AlbumController {

    private final AlbumServiceImpl albumService;
    private final SongServiceImpl songService;

    @GetMapping
    public ResponseEntity<List<AlbumResponse>> getAllAlbums() {
        List<AlbumResponse> albums = albumService.getAllAlbums();
        return ResponseEntity.ok(albums);
    }

    @GetMapping("/{keyword}")
    public ResponseEntity<List<AlbumResponse>> searchAlbums(@PathVariable String keyword) {
        List<AlbumResponse> filteredAlbums = albumService.searchAlbums(keyword);
        return ResponseEntity.ok(filteredAlbums);
    }

    @GetMapping("/songs/{albumId}")
    public ResponseEntity<List<SongResponse>> viewAllSongsInAlbum(@PathVariable String albumId) {
        List<SongResponse> songs = songService.viewAllSongsInAlbum(albumId);
        return ResponseEntity.ok(songs);
    }
}
