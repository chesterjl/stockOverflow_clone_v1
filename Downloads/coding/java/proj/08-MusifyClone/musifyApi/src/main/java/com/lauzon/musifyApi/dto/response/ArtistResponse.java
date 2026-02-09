package com.lauzon.musifyApi.dto.response;

import com.lauzon.musifyApi.enums.ArtistStatus;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ArtistResponse {

    private String id;
    private String userId;
    private String stageName;
    private String proofUrl;
    private ArtistStatus status;
    private String message;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
