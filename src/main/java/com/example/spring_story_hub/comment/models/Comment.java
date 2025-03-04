package com.example.spring_story_hub.comment.models;

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
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    //content, createdAt, user_id, story_id;

    @Column(nullable = false)
    private String content;
    @Column(nullable = false)
    private LocalDateTime createdOn;

    @ManyToOne
    private Story story;

    @ManyToOne
    private User owner;
}
