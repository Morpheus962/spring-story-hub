package com.example.spring_story_hub.like.repository;

import com.example.spring_story_hub.like.models.StoryLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface StoryLikeRepository extends JpaRepository<StoryLike, UUID> {
}
