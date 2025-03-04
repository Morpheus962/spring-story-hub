package com.example.spring_story_hub.web.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    @Size(min = 6, message = "Username must be atleast 6 characters.")
    private String username;
    @Size(min = 6, message = "Password must be atleast 6 characters.")
    private String password;

}
