package com.lauzon.musifyApi.document;

import com.lauzon.musifyApi.enums.ArtistStatus;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "artist")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ArtistDocument {

    @Id
    private String id;

    private String userId;

    private String stageName;
    private String proofUrl;
    private ArtistStatus status;
    private String message;

    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;
}


