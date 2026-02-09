package com.lauzon.musifyApi.repository;

import com.lauzon.musifyApi.document.ArtistDocument;
import com.lauzon.musifyApi.enums.ArtistStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ArtistRepository extends MongoRepository<ArtistDocument, String> {

    boolean existsByUserIdAndStatus(String userId, ArtistStatus status);

    Optional<ArtistDocument> findByUserId(String userId);
}
