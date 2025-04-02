package com.example.spring_story_hub.story.service;

import com.example.spring_story_hub.exception.DomainException;
import com.example.spring_story_hub.like.models.StoryLike;
import com.example.spring_story_hub.like.repository.StoryLikeRepository;
import com.example.spring_story_hub.notification.service.NotificationService;
import com.example.spring_story_hub.report.models.Report;
import com.example.spring_story_hub.story.models.Story;
import com.example.spring_story_hub.story.repository.StoryRepository;
import com.example.spring_story_hub.user.models.User;
import com.example.spring_story_hub.user.repository.UserRepository;
import com.example.spring_story_hub.user.service.UserService;
import com.example.spring_story_hub.web.dto.CreateReportRequest;
import com.example.spring_story_hub.web.dto.CreateStoryRequest;
import com.example.spring_story_hub.web.dto.EditStoryRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class StoryService {
    private final StoryRepository storyRepository;
    private final UserService userService;
    private final StoryLikeRepository storyLikeRepository;
    private final NotificationService notificationService;

    @Autowired
    public StoryService(StoryRepository storyRepository, UserRepository userRepository, UserService userService, StoryLikeRepository storyLikeRepository, NotificationService notificationService) {
        this.storyRepository = storyRepository;
        this.userService = userService;

        this.storyLikeRepository = storyLikeRepository;
        this.notificationService = notificationService;
    }

    public List<Story> findAllStories() {

        return storyRepository.findAll();

    }


    public Story findById(UUID id) {
        return storyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("There is no story with id [%s]".formatted(id)));

    }

    public Story createStory(User user, CreateStoryRequest createStoryRequest) {
        Story story = Story.builder()
                .createdOn(LocalDateTime.now())
                .genre(createStoryRequest.getGenre())
                .title(createStoryRequest.getTitle())
                .content(createStoryRequest.getContent())
                .owner(user)
                .build();


        return storyRepository.save(story);
    }


    public Story getById(UUID id) {
        return storyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Story with id [%s] does not exist.".formatted(id)));

    }

    public void editStory(UUID id, EditStoryRequest editStoryRequest) {
        Story story = getById(id);
        story.setContent(editStoryRequest.getContent());
        story.setTitle(editStoryRequest.getTitle());
        story.setGenre(editStoryRequest.getGenre());
        storyRepository.save(story);

    }

    public void likeStory(UUID storyId, UUID userId) {
        Story story = getById(storyId);
        User user = userService.getById(userId);
        Optional<StoryLike> optionalStoryLike = storyLikeRepository.findByUserAndStory(user, story);
        if (optionalStoryLike.isPresent()){
            throw new DomainException("Username with id [%s] already liked this story.".formatted(user.getId()));
        }
        StoryLike like = StoryLike.builder()
                .story(story)
                .user(user)
                .build();
        story.getStoryLikes().add(like);
        storyRepository.save(story);
        String emailBody = "%s liked your story.".formatted(user.getUsername());
        notificationService.sendNotification(story.getOwner().getId(), "New Like", emailBody);

    }

    @Cacheable("stories")
    public List<Story> getAllStories() {
        return storyRepository.findAll();

    }
}
