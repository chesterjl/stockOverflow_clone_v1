package com.lauzon.stackOverflow.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AnswerRequest {

    @NotBlank(message = "Answer is required")
    private String description;

    @NotNull(message = "Question id is required")
    private Long questionId;
}
