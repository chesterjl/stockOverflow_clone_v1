package com.lauzon.musifyApi.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArtistRequest {

    @NotBlank(message = "Stage name is required")
    private String stageName;

    @NotBlank(message = "Proof of identity or portfolio link is required")
    private String proofUrl;

    @Size(max = 400, message = "Max is 400 characters only")
    private String message;
}
