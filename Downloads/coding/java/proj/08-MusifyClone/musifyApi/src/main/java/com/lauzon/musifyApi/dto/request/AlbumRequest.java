package com.lauzon.musifyApi.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AlbumRequest {

    @NotBlank(message = "Album name is required")
    @Size(max = 70, message = "Album name must 70 max characters only")
    private String name;
    private String description;
    private MultipartFile imageFile;
}
