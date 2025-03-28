package com.example.spring_story_hub.scheduler;

import com.example.spring_story_hub.exception.DomainException;
import com.example.spring_story_hub.story.models.Story;
import com.example.spring_story_hub.story.service.StoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Component
public class StoryScheduler {

    private final StoryService storyService;

    @Autowired
    public StoryScheduler(StoryService storyService) {
        this.storyService = storyService;

    }

    private List<Story> cachedTopStories = List.of();


    @Scheduled(fixedRate = 60000)
    public void refreshTopStories(){
        List<Story> allStories = storyService.getAllStories();

        cachedTopStories = allStories.stream()
                .sorted(Comparator.comparingLong((Story story) -> story.getStoryLikes().size()).reversed())
                .limit(10)
                .toList();

        log.info("Top stories refreshed at {}",LocalDateTime.now());
    }

    public List<Story> getCachedTopStories() {
        if (cachedTopStories.isEmpty()){
            throw new DomainException("There are no liked stories.");
        }
        return cachedTopStories;
    }
}
