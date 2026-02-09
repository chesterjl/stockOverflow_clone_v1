package com.lauzon.musifyApi.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class UserResponse {

    private String id;
    private String username;
    private String email;
    private String role;
    private LocalDateTime createdAt;

}
