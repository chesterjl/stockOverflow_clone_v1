package com.lauzon.musifyApi.repository;

import com.lauzon.musifyApi.document.AlbumDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface AlbumRepository extends MongoRepository<AlbumDocument, String> {

    List<AlbumDocument> findAllByArtistName(String artistName);

    Optional<AlbumDocument> findByIdAndArtistName(String albumId, String artistName);

    List<AlbumDocument> findByNameContainingIgnoreCase(String keyword);
}
