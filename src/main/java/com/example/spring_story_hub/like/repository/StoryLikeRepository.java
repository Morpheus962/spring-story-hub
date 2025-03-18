package com.example.spring_story_hub.like.repository;

import com.example.spring_story_hub.like.models.StoryLike;
import com.example.spring_story_hub.story.models.Story;
import com.example.spring_story_hub.user.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StoryLikeRepository extends JpaRepository<StoryLike, UUID> {

    Optional<StoryLike> findByUserAndStory(User user, Story story);
}
