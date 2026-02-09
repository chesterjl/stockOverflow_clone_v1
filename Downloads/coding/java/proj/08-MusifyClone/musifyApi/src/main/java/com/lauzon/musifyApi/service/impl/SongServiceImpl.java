package com.lauzon.musifyApi.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.lauzon.musifyApi.document.AlbumDocument;
import com.lauzon.musifyApi.document.ArtistDocument;
import com.lauzon.musifyApi.document.SongDocument;
import com.lauzon.musifyApi.dto.request.SongRequest;
import com.lauzon.musifyApi.dto.response.SongResponse;
import com.lauzon.musifyApi.exceptions.ResourceNotFoundException;
import com.lauzon.musifyApi.repository.AlbumRepository;
import com.lauzon.musifyApi.repository.SongRepository;
import com.lauzon.musifyApi.service.SongService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SongServiceImpl implements SongService {

    private final SongRepository songRepository;
    private final Cloudinary cloudinary;
    private final AlbumRepository albumRepository;
    private final ArtistServiceImpl artistService;

    @Override
    public SongResponse addSong(SongRequest request) throws IOException {
        Map<String, Object> audioUploadResult = cloudinary.uploader().upload(request.getAudioFileUrl().getBytes(), ObjectUtils.asMap("resource_type", "video"));
        Map<String, Object> imageUploadResult = cloudinary.uploader().upload(request.getImageUrl().getBytes(), ObjectUtils.asMap("resource_type", "image"));

        Double durationSeconds = (Double) audioUploadResult.get("duration");
        String duration = formatDuration(durationSeconds);

        String albumName, audio, image, audioPublicId, imagePublicId;

        audio = audioUploadResult.get("secure_url").toString();
        image = imageUploadResult.get("secure_url").toString();
        audioPublicId = (String) audioUploadResult.get("public_id");
        imagePublicId = (String) imageUploadResult.get("public_id");

        ArtistDocument artist = artistService.getCurrentArtist();

        if (request.getAlbumId() == null || request.getAlbumId().isEmpty()) {
            albumName = request.getName();
        } else {
            AlbumDocument album = albumRepository.findByIdAndArtistName(request.getAlbumId(), artist.getStageName())
                    .orElseThrow(() -> new ResourceNotFoundException("Album not found"));
            albumName = album.getName();
        }

        SongDocument newSong = convertToDocument(request, duration, albumName, artist.getStageName(), image, audio, audioPublicId, imagePublicId);
        newSong = songRepository.save(newSong);
        return convertToResponse(newSong);
    }

    @Override
    public SongResponse updateSong(SongRequest request, String songId) throws IOException {
        ArtistDocument artist = artistService.getCurrentArtist();

        SongDocument existingSong = songRepository.findByIdAndArtistName(songId, artist.getStageName())
                .orElseThrow(() -> new ResourceNotFoundException("Song not found update"));

        // 3️⃣ Update simple fields (name, description)
        if (request.getName() != null && !request.getName().isBlank()) {
            existingSong.setName(request.getName());
        }
        if (request.getDescription() != null) {
            existingSong.setDescription(request.getDescription());
        }

        // 4️⃣ Update album name logic
        if (request.getAlbumId() == null || request.getAlbumId().isEmpty()) {
            existingSong.setAlbumName(request.getName()); // treat as single
        } else {
            AlbumDocument album = albumRepository.findByIdAndArtistName(request.getAlbumId(), artist.getStageName())
                    .orElseThrow(() -> new ResourceNotFoundException("Album not found"));
            existingSong.setAlbumName(album.getName());
        }

        // 5️⃣ Handle optional new uploads (if user re-uploads audio or image)
        if (request.getAudioFileUrl() != null && !request.getAudioFileUrl().isEmpty()) {
            if (existingSong.getAudioPublicId() != null) {
                cloudinary.uploader().destroy(existingSong.getAudioPublicId(), ObjectUtils.asMap("resource_type", "video"));
            }
            Map<String, Object> audioUploadResult = cloudinary.uploader().upload(
                    request.getAudioFileUrl().getBytes(),
                    ObjectUtils.asMap("resource_type", "video")
            );
            existingSong.setAudio(audioUploadResult.get("secure_url").toString());
            existingSong.setAudioPublicId((String) audioUploadResult.get("public_id"));

            Double durationSeconds = (Double) audioUploadResult.get("duration");
            String duration = formatDuration(durationSeconds);
            existingSong.setDuration(duration);
        }

        if (request.getImageUrl() != null && !request.getImageUrl().isEmpty()) {
            if (existingSong.getImagePublicId() != null) {
                cloudinary.uploader().destroy(existingSong.getImagePublicId(), ObjectUtils.asMap("resource_type", "image"));
            }
            Map<String, Object> imageUploadResult = cloudinary.uploader().upload(
                    request.getImageUrl().getBytes(),
                    ObjectUtils.asMap("resource_type", "image")
            );
            existingSong.setImage(imageUploadResult.get("secure_url").toString());
            existingSong.setImagePublicId((String) imageUploadResult.get("public_id"));
        }

        // 7️⃣ Save and return response
        existingSong = songRepository.save(existingSong);
        return convertToResponse(existingSong);
    }


    @Override
    public Map<String, String> deleteSong(String songId) {
        ArtistDocument artist = artistService.getCurrentArtist();

        SongDocument song = songRepository.findByIdAndArtistName(songId, artist.getStageName())
                .orElseThrow(() -> new ResourceNotFoundException("Song not found to delete"));

        try {
            // Delete audio file
            if (song.getAudioPublicId() != null) {
                cloudinary.uploader().destroy(song.getAudioPublicId(),
                        ObjectUtils.asMap("resource_type", "video"));
            }

            // Delete image file
            if (song.getImagePublicId() != null) {
                cloudinary.uploader().destroy(song.getImagePublicId(),
                        ObjectUtils.asMap("resource_type", "image"));
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete song from Cloudinary: " + e.getMessage());
        }

        songRepository.delete(song);

        return Map.of("message", "Successfully deleted song and removed files from Cloudinary");
    }


    @Override
    public List<SongResponse> viewAllSongs() {
        List<SongDocument> songs = songRepository.findAll();
        return songs.stream().map(this::convertToResponse).toList();
    }

    @Override
    public List<SongResponse> viewSongByName(String name) {
        List<SongDocument> songs = songRepository.findAllByName(name);
        return songs.stream().map(this::convertToResponse).toList();
    }

    @Override
    public List<SongResponse> searchSongs(String keyword) {
        List<SongDocument> songsFiltered = songRepository.findByNameContainingIgnoreCase(keyword);
        return songsFiltered.stream().map(this::convertToResponse).toList();
    }

    @Override
    public List<SongResponse> viewAllSongsForCurrentArtist() {
        ArtistDocument artist = artistService.getCurrentArtist();
        List<SongDocument> songs = songRepository.findAllByArtistName(artist.getStageName());
        return songs.stream().map(this::convertToResponse).toList();
    }

    @Override
    public List<SongResponse> viewAllSongsInAlbum(String albumId) {
        List<SongDocument> songs = songRepository.findAllByAlbumId(albumId);
        return songs.stream().map(this::convertToResponse).toList();
    }

    private String formatDuration(Double durationSeconds) {
        if (durationSeconds == null) {
            return "0:00";
        }
        int minutes = (int) (durationSeconds / 60);
        int seconds = (int) (durationSeconds % 60);
        return String.format("%d:%02d", minutes, seconds);
    }

    private SongDocument convertToDocument(
            SongRequest request, String duration, String albumName, String artistName,
            String image, String audio, String audioPublicId, String imagePublicId
    ) {
        return SongDocument.builder()
                .artistName(artistName)
                .name(request.getName())
                .description(request.getDescription())
                .albumName(albumName)
                .albumId(request.getAlbumId())
                .image(image)
                .audio(audio)
                .audioPublicId(audioPublicId)
                .imagePublicId(imagePublicId)
                .duration(duration)
                .build();
    }

    private SongResponse convertToResponse(SongDocument songDocument) {
        return SongResponse.builder()
                .id(songDocument.getId())
                .artistName(songDocument.getArtistName())
                .name(songDocument.getName())
                .description(songDocument.getDescription())
                .albumName(songDocument.getAlbumName())
                .albumId(songDocument.getAlbumId() != null ? songDocument.getAlbumId() : "N/A")
                .image(songDocument.getImage())
                .audio(songDocument.getAudio())
                .duration(songDocument.getDuration())
                .createdAt(songDocument.getCreatedAt())
                .updatedAt(songDocument.getUpdatedAt())
                .build();
    }

}
