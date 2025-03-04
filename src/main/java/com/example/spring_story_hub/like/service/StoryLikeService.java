package com.example.spring_story_hub.like.service;

import com.example.spring_story_hub.like.repository.StoryLikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StoryLikeService {
    private final StoryLikeRepository storyLikeRepository;

    @Autowired
    public StoryLikeService(StoryLikeRepository storyLikeRepository) {
        this.storyLikeRepository = storyLikeRepository;
    }
}
