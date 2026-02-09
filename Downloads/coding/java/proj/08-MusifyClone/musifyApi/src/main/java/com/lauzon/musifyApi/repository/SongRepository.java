package com.lauzon.musifyApi.repository;

import com.lauzon.musifyApi.document.SongDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface SongRepository extends MongoRepository<SongDocument, String> {

    Optional<SongDocument> findByIdAndArtistName(String songId, String artistName);

    List<SongDocument> findAllByArtistName(String artistName);

    List<SongDocument> findAllByName(String name);

    List<SongDocument> findByNameContainingIgnoreCase(String keyword);

    List<SongDocument> findAllByAlbumId(String albumId);
}