package com.example.spring_story_hub.comment.service;

import com.example.spring_story_hub.comment.models.Comment;
import com.example.spring_story_hub.comment.repository.CommentRepository;
import com.example.spring_story_hub.exception.DomainException;
import com.example.spring_story_hub.story.repository.StoryRepository;
import com.example.spring_story_hub.story.service.StoryService;
import com.example.spring_story_hub.user.models.User;
import com.example.spring_story_hub.web.dto.CommentRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final StoryService storyService;

    @Autowired
    public CommentService(CommentRepository commentRepository, StoryRepository storyRepository, StoryService storyService) {

        this.commentRepository = commentRepository;
        this.storyService = storyService;

    }


    public List<Comment> getCommentsByStory(UUID storyId) {
        List<Comment> comments = commentRepository.findByStoryId(storyId);
        return comments;
    }

    public Comment createComment(User user, CommentRequest commentRequest, UUID storyId) {
        Comment comment = Comment.builder()
                .owner(user)
                .content(commentRequest.getContent())
                .story(storyService.getById(storyId))
                .createdOn(LocalDateTime.now())
                .build();
       return commentRepository.save(comment);
    }
}
