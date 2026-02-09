package com.lauzon.musifyApi.dto.response;

import lombok.*;

import java.time.LocalDateTime;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AlbumResponse {

    private String id;
    private String artistName;
    private String name;
    private String description;
    private String image;
    private String imagePublicId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
