package com.example.spring_story_hub.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommentRequest {
    @NotNull
    @Size(max = 1000, message = "Comment must not exceed 1000 characters.")
    private String content;
}
