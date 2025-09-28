package com.lauzon.stackOverflow.dto.response;


import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuestionResponse {

    private Long id;
    private String title;
    private String description;
    private String name;
    private Long userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
