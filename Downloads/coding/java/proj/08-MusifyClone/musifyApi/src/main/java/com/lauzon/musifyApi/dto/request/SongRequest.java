package com.lauzon.musifyApi.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class SongRequest {

    @NotBlank(message = "Song name is required")
    private String name; // song name
    @Size(max = 300, message = "Song description is 400 max characters only")
    private String description;
    private String albumId;
    @NotNull(message = "Song image is required")
    private MultipartFile imageUrl; // song image
    @NotNull(message = "Song audio is required")
    private MultipartFile audioFileUrl; // audio file
}
