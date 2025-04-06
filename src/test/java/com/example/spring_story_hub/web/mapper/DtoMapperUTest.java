package com.example.spring_story_hub.web.mapper;

import com.example.spring_story_hub.story.models.Genre;
import com.example.spring_story_hub.story.models.Story;
import com.example.spring_story_hub.user.models.User;
import com.example.spring_story_hub.web.dto.EditStoryRequest;
import com.example.spring_story_hub.web.dto.EditUserRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class DtoMapperUTest {

    @Test
    void givenHappyPath_whenMappingUserToUserEditRequest() {

        // Given
        User user = User.builder()
                .firstName("Ivan")
                .lastName("Ivanov")
                .email("ivan123@abv.bg")
                .profilePicture("www.image.com")
                .build();

        // When
        EditUserRequest resultDto = DtoMapper.mapToEditUserRequest(user);

        // Then
        assertEquals(user.getFirstName(), resultDto.getFirstName());
        assertEquals(user.getLastName(), resultDto.getLastName());
        assertEquals(user.getEmail(), resultDto.getEmail());
        assertEquals(user.getProfilePicture(), resultDto.getProfilePicture());
    }

    @Test
    void givenHappyPath_whenMappingStoryToStoryRequest(){
        User user = User.builder()
                .firstName("Ivan")
                .lastName("Ivanov")
                .email("ivan123@abv.bg")
                .profilePicture("www.image.com")
                .build();
        Story story = Story.builder()
                .title("title")
                .content("content")
                .owner(user)
                .storyLikes(new ArrayList<>())
                .genre(Genre.HORROR)
                .id(UUID.randomUUID())
                .build();

        EditStoryRequest editStoryRequest = DtoMapper.mapToStoryRequest(story);
        assertEquals(story.getContent(), editStoryRequest.getContent());
        assertEquals(story.getTitle(), editStoryRequest.getTitle());
        assertEquals(story.getGenre(), editStoryRequest.getGenre());
    }
}