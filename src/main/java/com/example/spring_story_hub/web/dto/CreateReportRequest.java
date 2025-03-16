package com.example.spring_story_hub.web.dto;

import com.example.spring_story_hub.story.models.Story;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateReportRequest {
    //reason, reporter, reportedStory

    @NotNull
    @Size(max = 1000, message = "Reason must not exceed 1000 characters.")
    private String reason;

}
