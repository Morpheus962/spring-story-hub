package com.example.spring_story_hub.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    @NotBlank
    @Size(min = 6, message = "Username must be at least 6 characters.")
    private String username;

    @NotBlank
    @Size(min = 6, message = "Password must be at least 6 characters.")
    private String password;

    @Email
    private String email;

}
