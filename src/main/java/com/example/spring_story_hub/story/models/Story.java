package com.example.spring_story_hub.story.models;

import com.example.spring_story_hub.comment.models.Comment;
import com.example.spring_story_hub.like.models.StoryLike;
import com.example.spring_story_hub.report.models.Report;
import com.example.spring_story_hub.user.models.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Story {
    //title, content, createdAt, genre, owner
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private LocalDateTime createdOn;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Genre genre;

    @ManyToOne
    private User owner;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "story")
    @OrderBy("createdOn DESC")
    private List<Comment> comments;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "story")
    private List<StoryLike> storyLikes;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "reportedStory")
    private List<Report> reports;
}
