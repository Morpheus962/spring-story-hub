package com.example.spring_story_hub.report.models;

import com.example.spring_story_hub.story.models.Story;
import com.example.spring_story_hub.user.models.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor

public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
     @Column(nullable = false)
    private String reason;
     @Column(nullable = false)
    private LocalDateTime createdOn;
     @ManyToOne
    private User reporter;

     @ManyToOne
    private Story reportedStory;
}
