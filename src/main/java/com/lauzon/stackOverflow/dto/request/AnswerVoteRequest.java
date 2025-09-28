package com.lauzon.stackOverflow.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AnswerVoteRequest {

    @NotNull(message = "Answer id is required")
    private Long answerId;


}
