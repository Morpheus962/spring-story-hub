package com.example.spring_story_hub.story;

import com.example.spring_story_hub.exception.DomainException;
import com.example.spring_story_hub.like.models.StoryLike;
import com.example.spring_story_hub.like.repository.StoryLikeRepository;
import com.example.spring_story_hub.notification.service.NotificationService;
import com.example.spring_story_hub.story.models.Genre;
import com.example.spring_story_hub.story.models.Story;
import com.example.spring_story_hub.story.repository.StoryRepository;
import com.example.spring_story_hub.story.service.StoryService;
import com.example.spring_story_hub.user.models.User;
import com.example.spring_story_hub.user.service.UserService;
import com.example.spring_story_hub.web.dto.CreateStoryRequest;
import com.example.spring_story_hub.web.dto.EditStoryRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StoryServiceUTest {
    @Mock
    private  StoryRepository storyRepository;
    @Mock
    private  UserService userService;
    @Mock
    private  StoryLikeRepository storyLikeRepository;
    @Mock
    private  NotificationService notificationService;

    @Spy
    @InjectMocks
    private StoryService storyService;

    @Test
    void givenNonExistentId_whenFindById_thenThrowException(){
        UUID storyId = UUID.randomUUID();
        when(storyRepository.findById(storyId)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> storyService.findById(storyId));

    }

    @Test
    void givenExistingId_whenFindById_thenReturnStory(){
        UUID storyId = UUID.randomUUID();
        Story story = Story.builder()
                .id(storyId)
                .build();
        when(storyRepository.findById(storyId)).thenReturn(Optional.of(story));
        Story result = storyService.findById(storyId);
        assertEquals(storyId, result.getId());
    }

    @Test
    void happyPathTo_findAllStories(){
        List<Story> stories = List.of(new Story(), new Story());
        when(storyRepository.findAll()).thenReturn(stories);
        List<Story> allStories = storyService.findAllStories();
        assertEquals(2, allStories.size());
    }

    @Test
    void givenUserAndDto_whenCreateStory_thenReturnCreatedStory(){
        CreateStoryRequest dto = new CreateStoryRequest();
        dto.setTitle("title");
        dto.setContent("content");
        dto.setGenre(Genre.HORROR);
        User user = User.builder()
                .id(UUID.randomUUID())
                .build();
        Story story = Story.builder()
                .id(UUID.randomUUID())
                .owner(user)
                .build();
        when(storyRepository.save(any())).thenReturn(story);
        Story createdStory = storyService.createStory(user, dto);
        assertEquals(story.getOwner(), createdStory.getOwner());
        assertEquals(story.getTitle(), createdStory.getTitle());
        assertEquals(story.getContent(), createdStory.getContent());
    }

    @Test
    void givenNonExistentStoryId_whenGetById_thenReturnException(){
        UUID userId = UUID.randomUUID();
        when(storyRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> storyService.getById(userId));
    }

    @Test
    void givenExistingStoryId_whenGetById_thenReturnStory(){
        UUID storyId = UUID.randomUUID();
        Story story = Story.builder()
                .id(storyId)
                .build();
        when(storyRepository.findById(storyId)).thenReturn(Optional.of(story));
        Story result = storyService.getById(storyId);
        assertEquals(storyId, result.getId());
    }

    @Test
    void givenStoryIdAndDto_whenEditStory_thenChangeStoryDetailsAndSaveToDatabase(){
        UUID storyId = UUID.randomUUID();
        EditStoryRequest dto = EditStoryRequest.builder()
                .title("title")
                .content("content")
                .genre(Genre.HORROR)
                .build();
        Story story = Story.builder()
                .id(storyId)
                .build();
        when(storyRepository.findById(storyId)).thenReturn(Optional.of(story));
        storyService.editStory(storyId, dto);
        assertEquals(story.getContent(), dto.getContent());
        assertEquals(story.getTitle(), dto.getTitle());
        assertEquals(story.getGenre(), dto.getGenre());
    }

    @Test
    void givenStoryIdAndUserId_whenLikeIsExistingAlready_thenReturnException(){
        UUID storyId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID userId = UUID.fromString("22222222-2222-2222-2222-222222222222");

        User user = User.builder()
                .id(userId)
                .username("testuser")
                .build();

        Story story = Story.builder()
                .id(storyId)
                .owner(User.builder().id(UUID.randomUUID()).build())
                .storyLikes(new ArrayList<>())
                .build();

        StoryLike like = StoryLike.builder()
                .user(user)
                .story(story)
                .build();

        doReturn(story).when(storyService).getById(storyId);
        when(userService.getById(userId)).thenReturn(user);
        when(storyLikeRepository.findByUserAndStory(user, story)).thenReturn(Optional.of(like));

        assertThrows(DomainException.class, () -> storyService.likeStory(storyId, userId));
    }

    @Test
    void givenStoryIdAndUserId_whenLikeIsNonExistent_thenLikeStory(){
        UUID userId = UUID.randomUUID();
        UUID storyId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .build();
        User owner = User.builder()
                .id(UUID.randomUUID())
                .build();
        Story story = Story.builder()
                .id(storyId)
                .storyLikes(new ArrayList<>())
                .owner(owner)
                .build();
        doReturn(story).when(storyService).getById(storyId);
        when(userService.getById(userId)).thenReturn(user);
        when(storyLikeRepository.findByUserAndStory(user, story)).thenReturn(Optional.empty());

        storyService.likeStory(storyId, userId);

        assertEquals(1, story.getStoryLikes().size());
        assertEquals(user, story.getStoryLikes().get(0).getUser());

        verify(storyRepository).save(story);
        verify(notificationService, times(1)).sendNotification(story.getOwner().getId(), "New Like", "%s liked your story.".formatted(user.getUsername()));

    }
}
