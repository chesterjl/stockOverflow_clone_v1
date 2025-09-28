package com.lauzon.stackOverflow.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AnswerVoteResponse {

    private Long id;
    private Long answerId;
    private Long userId;
    private String name;
    private LocalDateTime createdAt;

}

