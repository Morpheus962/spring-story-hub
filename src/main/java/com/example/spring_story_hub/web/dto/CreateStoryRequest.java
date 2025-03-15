package com.example.spring_story_hub.web.dto;

import com.example.spring_story_hub.story.models.Genre;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data

public class CreateStoryRequest {
    @NotNull
    @Size(max = 30, message = "Title must not exceed 30 characters.")
    private String title;

    @NotNull
    @Size(max = 1000, message = "Content must not exceed 1000 characters.")
    private String content;

    @NotNull
    private Genre genre;

}
