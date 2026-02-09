package com.lauzon.musifyApi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lauzon.musifyApi.dto.request.AlbumRequest;
import com.lauzon.musifyApi.dto.request.SongRequest;
import com.lauzon.musifyApi.dto.response.AlbumResponse;
import com.lauzon.musifyApi.dto.response.SongResponse;
import com.lauzon.musifyApi.service.impl.AlbumServiceImpl;
import com.lauzon.musifyApi.service.impl.SongServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@PreAuthorize("hasRole('ARTIST')")
@RequestMapping("/artists")
public class ArtistController {

    private final SongServiceImpl songService;
    private final AlbumServiceImpl albumService;

    @GetMapping
    public ResponseEntity<List<SongResponse>> viewAllSongsForCurrentArtist() {
        List<SongResponse> songs = songService.viewAllSongsForCurrentArtist();
        return ResponseEntity.ok(songs);
    }

    @PostMapping
    public ResponseEntity<?> addSong(@RequestPart("request") String requestString,
                                     @RequestPart("audio") MultipartFile audioFile,
                                     @RequestPart("image") MultipartFile imagFile) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            SongRequest songRequest = objectMapper.readValue(requestString, SongRequest.class);
            songRequest.setAudioFileUrl(audioFile);
            songRequest.setImageUrl(imagFile);
            SongResponse savedSong = songService.addSong(songRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedSong);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{songId}")
    public ResponseEntity<?> updateSong(@RequestPart("request") String requestString,
                                        @RequestPart("audio") MultipartFile audioFile,
                                        @RequestPart("image") MultipartFile imagFile,
                                        @PathVariable String songId) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            SongRequest songRequest = objectMapper.readValue(requestString, SongRequest.class);
            songRequest.setAudioFileUrl(audioFile);
            songRequest.setImageUrl(imagFile);
            SongResponse updatedSong = songService.updateSong(songRequest, songId);
            return ResponseEntity.status(HttpStatus.CREATED).body(updatedSong);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{songId}")
    public ResponseEntity<Map<String, String>> deleteSong(@PathVariable String songId) {
        Map<String, String> response = songService.deleteSong(songId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/albums")
    public ResponseEntity<List<AlbumResponse>> getAllAlbumsForArtist() {
        List<AlbumResponse> albums = albumService.getAllAlbumsForArtist();
        return ResponseEntity.ok(albums);
    }

    @PostMapping("/albums")
    public ResponseEntity<?> addAlbum(@RequestPart("request") String requestString,
                                      @RequestPart("image") MultipartFile imageFile) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            AlbumRequest albumRequest = objectMapper.readValue(requestString, AlbumRequest.class);
            albumRequest.setImageFile(imageFile);
            AlbumResponse newAlbum = albumService.addAlbum(albumRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(newAlbum);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/albums/{albumId}")
    public ResponseEntity<?> updateAlbum(@RequestPart("request") String requestString,
                                         @RequestPart("image") MultipartFile imageFile,
                                         @PathVariable String albumId) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            AlbumRequest albumRequest = objectMapper.readValue(requestString, AlbumRequest.class);
            albumRequest.setImageFile(imageFile);
            AlbumResponse updatedAlbum = albumService.updateAlbum(albumRequest, albumId);
            return ResponseEntity.ok(updatedAlbum);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/albums/{albumId}")
    public ResponseEntity<Void> removeAlbum(@PathVariable String albumId) {
        albumService.removeAlbum(albumId);
        return ResponseEntity.ok().build();
    }
}
