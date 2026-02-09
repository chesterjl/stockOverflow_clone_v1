package com.lauzon.musifyApi.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class SongResponse {

    private String id;
    private String artistName; // artist name created the song
    private String name; // song name
    private String description;
    private String albumName;
    private String albumId;
    private String image; // song image
    private String audio; // audio file
    private String duration;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
