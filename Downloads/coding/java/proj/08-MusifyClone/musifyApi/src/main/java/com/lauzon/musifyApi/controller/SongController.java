package com.lauzon.musifyApi.controller;

import com.lauzon.musifyApi.dto.response.AlbumResponse;
import com.lauzon.musifyApi.dto.response.SongResponse;
import com.lauzon.musifyApi.service.AlbumService;
import com.lauzon.musifyApi.service.impl.SongServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/songs")
public class SongController {

    private final SongServiceImpl songService;

    @GetMapping
    public ResponseEntity<List<SongResponse>> viewAllSongs() {
     List<SongResponse> songs = songService.viewAllSongs();
     return ResponseEntity.ok(songs);
    }

    @GetMapping("/{name}")
    public ResponseEntity<List<SongResponse>> viewSongsByName(@PathVariable String name) {
        List<SongResponse> songs = songService.viewSongByName(name);
        return ResponseEntity.ok(songs);
    }

    @GetMapping("/search/{keyword}")
    public ResponseEntity<List<SongResponse>> searchSongs(@PathVariable String keyword) {
        List<SongResponse> songs = songService.searchSongs(keyword);
        return ResponseEntity.ok(songs);
    }

}
