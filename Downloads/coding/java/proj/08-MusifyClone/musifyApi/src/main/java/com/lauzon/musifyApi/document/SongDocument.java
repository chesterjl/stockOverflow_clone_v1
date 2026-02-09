package com.lauzon.musifyApi.document;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "songs")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class SongDocument {

    @Id
    private String id;
    private String artistName; // artist name created the song
    private String name; // song name
    private String description;
    private String albumName;
    private String albumId;
    private String image; // song image
    private String audio; // audio file
    private String audioPublicId;
    private String imagePublicId;
    private String duration;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}


