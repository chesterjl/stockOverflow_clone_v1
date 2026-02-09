package com.lauzon.musifyApi.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.lauzon.musifyApi.document.AlbumDocument;
import com.lauzon.musifyApi.document.ArtistDocument;
import com.lauzon.musifyApi.dto.request.AlbumRequest;
import com.lauzon.musifyApi.dto.response.AlbumResponse;
import com.lauzon.musifyApi.exceptions.ResourceNotFoundException;
import com.lauzon.musifyApi.repository.AlbumRepository;
import com.lauzon.musifyApi.service.AlbumService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AlbumServiceImpl implements AlbumService {

    private final Cloudinary cloudinary;
    private final AlbumRepository albumRepository;
    private final ArtistServiceImpl artistService;

    @Override
    public AlbumResponse addAlbum(AlbumRequest request) throws IOException {
        Map<String, Object> imageUploadResult = cloudinary.uploader().upload(request.getImageFile().getBytes(), ObjectUtils.asMap("resource_type", "image"));

        String image = imageUploadResult.get("secure_url").toString();
        String imagePublicId = (String) imageUploadResult.get("public_id");

        ArtistDocument artist = artistService.getCurrentArtist();

        AlbumDocument newAlbum = convertToDocument(request, image, imagePublicId, artist.getStageName());
        newAlbum = albumRepository.save(newAlbum);
        return convertToResponse(newAlbum);
    }

    @Override
    public AlbumResponse updateAlbum(AlbumRequest request, String albumId) throws IOException {
        ArtistDocument artist = artistService.getCurrentArtist();

        AlbumDocument existingAlbum = albumRepository.findByIdAndArtistName(albumId, artist.getStageName())
                .orElseThrow(() -> new ResourceNotFoundException("Album not found"));


        if (request.getName() != null && !request.getName().isBlank()) {
            existingAlbum.setName(request.getName());
        }

        if (request.getDescription() != null && !request.getDescription().isBlank()) {
            existingAlbum.setDescription(request.getDescription());
        }

        if (request.getImageFile() != null && !request.getImageFile().isEmpty()) {
            if (existingAlbum.getImagePublicId() != null) {
                cloudinary.uploader().destroy(existingAlbum.getImagePublicId(), ObjectUtils.asMap("resource_type", "image"));
            }
            Map<String, Object> imageUploadResult = cloudinary.uploader().upload(
                    request.getImageFile().getBytes(),
                    ObjectUtils.asMap("resource_type", "image")
            );
            existingAlbum.setImage(imageUploadResult.get("secure_url").toString());
            existingAlbum.setImagePublicId((String) imageUploadResult.get("public_id"));
        }

        existingAlbum = albumRepository.save(existingAlbum);
        return convertToResponse(existingAlbum);
    }

    @Override
    public List<AlbumResponse> getAllAlbumsForArtist() {
        ArtistDocument artist = artistService.getCurrentArtist();
        List<AlbumDocument> albums = albumRepository.findAllByArtistName(artist.getStageName());
        return albums.stream().map(this::convertToResponse).toList();
    }

    @Override
    public void removeAlbum(String albumId) {
        ArtistDocument artist = artistService.getCurrentArtist();
        AlbumDocument existingAlbum = albumRepository.findByIdAndArtistName(albumId, artist.getStageName())
                .orElseThrow(() -> new ResourceNotFoundException("Album not found"));
        albumRepository.delete(existingAlbum);
    }

    @Override
    public List<AlbumResponse> getAllAlbums() {
        List<AlbumDocument> albums = albumRepository.findAll();
        return albums.stream().map(this::convertToResponse).toList();
    }

    @Override
    public List<AlbumResponse> searchAlbums(String keyword) {
        List<AlbumDocument> filteredAlbums = albumRepository.findByNameContainingIgnoreCase(keyword);
        return filteredAlbums.stream().map(this::convertToResponse).toList();
    }

    private AlbumDocument convertToDocument(AlbumRequest request, String image, String imagePublicId, String artistName) {
        return AlbumDocument.builder()
                .artistName(artistName)
                .name(request.getName())
                .image(image)
                .imagePublicId(imagePublicId)
                .description(request.getDescription())
                .build();
    }

    private AlbumResponse convertToResponse(AlbumDocument album) {
        return AlbumResponse.builder()
                .id(album.getId())
                .artistName(album.getArtistName())
                .name(album.getName())
                .description(album.getDescription())
                .image(album.getImage())
                .imagePublicId(album.getImagePublicId())
                .createdAt(album.getCreatedAt())
                .updatedAt(album.getUpdatedAt())
                .build();
    }
}
