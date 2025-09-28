package com.lauzon.stackOverflow.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateAnswerRequest {

    @NotBlank(message = "Answer is required")
    private String description;

}
