package com.lauzon.stackOverflow.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AnswerResponse {

    private Long id;
    private Long questionId;
    private String description;
    private String name; // commented
    private Long userId;
    private Long upvote;
    private Long downvote;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
