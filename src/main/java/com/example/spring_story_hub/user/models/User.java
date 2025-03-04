package com.example.spring_story_hub.user.models;

import com.example.spring_story_hub.comment.models.Comment;
import com.example.spring_story_hub.like.models.StoryLike;
import com.example.spring_story_hub.report.models.Report;
import com.example.spring_story_hub.story.models.Story;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Builder
@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    //username, password, email, firstName, lastName, role
    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false)
    private String password;
    @Column(unique = true)
    private String email;

    private String firstName;

    private String lastName;

    private String profilePicture;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private Role role;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "owner", cascade = CascadeType.ALL)
    @OrderBy("createdOn DESC")
    private List<Story> stories = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "owner")
    @OrderBy("createdOn DESC")
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "user")
    private List<StoryLike> storyLikes;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "reporter")
    private List<Report> reports;

}
