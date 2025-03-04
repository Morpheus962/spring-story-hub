package com.example.spring_story_hub.like.models;

import com.example.spring_story_hub.story.models.Story;
import com.example.spring_story_hub.user.models.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Builder
@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class StoryLike {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Story story;
}
