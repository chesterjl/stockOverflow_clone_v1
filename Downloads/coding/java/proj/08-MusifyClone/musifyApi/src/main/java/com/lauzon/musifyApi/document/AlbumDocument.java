package com.lauzon.musifyApi.document;

import lombok.*;
import org.springframework.cglib.core.Local;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "albums")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AlbumDocument {

    @Id
    private String id;

    private String artistName;
    private String name;
    private String description;
    private String image;
    private String imagePublicId;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
