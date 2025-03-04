package com.example.spring_story_hub.story.service;

import com.example.spring_story_hub.story.repository.StoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StoryService {
    private final StoryRepository storyRepository;

    @Autowired
    public StoryService(StoryRepository storyRepository) {
        this.storyRepository = storyRepository;
    }
}
