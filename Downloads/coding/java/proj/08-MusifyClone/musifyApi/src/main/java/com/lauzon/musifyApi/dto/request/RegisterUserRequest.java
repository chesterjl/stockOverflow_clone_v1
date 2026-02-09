package com.lauzon.musifyApi.dto.request;

import com.lauzon.musifyApi.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class RegisterUserRequest {

    @Size(max = 50, message = "Username max is 50 characters")
    private String username;
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;
    @Size(min = 6, max = 30, message = "Password is required")
    @NotBlank(message = "Password is required")
    private String password;
    private Role role;
}
