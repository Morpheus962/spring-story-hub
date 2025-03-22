package com.example.spring_story_hub.comment.repository;

import com.example.spring_story_hub.comment.models.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {
    List<Comment> findByStoryId(UUID storyId);
}
