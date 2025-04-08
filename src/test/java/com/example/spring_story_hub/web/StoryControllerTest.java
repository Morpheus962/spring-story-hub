package com.example.spring_story_hub.web;

import com.example.spring_story_hub.comment.models.Comment;
import com.example.spring_story_hub.comment.service.CommentService;
import com.example.spring_story_hub.like.models.StoryLike;
import com.example.spring_story_hub.like.repository.StoryLikeRepository;
import com.example.spring_story_hub.notification.service.NotificationService;
import com.example.spring_story_hub.report.service.ReportService;
import com.example.spring_story_hub.scheduler.StoryScheduler;
import com.example.spring_story_hub.security.AuthenticationMetaData;
import com.example.spring_story_hub.story.models.Genre;
import com.example.spring_story_hub.story.models.Story;
import com.example.spring_story_hub.story.repository.StoryRepository;
import com.example.spring_story_hub.story.service.StoryService;
import com.example.spring_story_hub.user.models.Role;
import com.example.spring_story_hub.user.models.User;
import com.example.spring_story_hub.user.service.UserService;
import com.example.spring_story_hub.web.controller.StoryController;
import com.example.spring_story_hub.web.dto.CreateReportRequest;
import com.example.spring_story_hub.web.dto.CreateStoryRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StoryController.class)
public class StoryControllerTest {

    @MockBean
    private StoryService storyService;
    @MockBean
    private  UserService userService;
    @MockBean
    private  ReportService reportService;
    @MockBean
    private  CommentService commentService;
    @MockBean
    private  StoryScheduler storyScheduler;
    @Mock
    private AuthenticationMetaData authenticationMetaData;

    @Autowired
    private MockMvc mockMvc;




    @Test
    void getStoriesPage_shouldReturnStoriesViewWithStoriesInModel() throws Exception {
        Story story = Story.builder()
                .id(UUID.randomUUID())
                .title("Test Story")
                .build();
        List<Story> stories = Collections.singletonList(story);
        when(storyService.findAllStories()).thenReturn(stories);

        AuthenticationMetaData principal = new AuthenticationMetaData(
                UUID.randomUUID(), "TestUser", "password", Role.USER, true
        );

        MockHttpServletRequestBuilder request = get("/stories")
                .with(user(principal));

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("stories"))
                .andExpect(model().attributeExists("stories"));
    }

    @Test
    void readStory_shouldReturnStoryDetailsViewWithModelAttributes() throws Exception {
        UUID storyId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        User owner = User.builder()
                .id(UUID.randomUUID())
                .username("Author")
                .build();

        User liker1 = User.builder()
                .id(UUID.randomUUID())
                .username("Alice")
                .build();

        User liker2 = User.builder()
                .id(UUID.randomUUID())
                .username("Bob")
                .build();

        StoryLike like1 = StoryLike.builder()
                .id(UUID.randomUUID())
                .user(liker1)
                .build();

        StoryLike like2 = StoryLike.builder()
                .id(UUID.randomUUID())
                .user(liker2)
                .build();

        Story story = Story.builder()
                .id(storyId)
                .title("Cool Story")
                .owner(owner)
                .storyLikes(List.of(like1, like2))
                .build();

        List<Comment> comments = List.of(); // empty for now

        when(storyService.findById(storyId)).thenReturn(story);
        when(commentService.getCommentsByStory(storyId)).thenReturn(comments);

        AuthenticationMetaData principal = new AuthenticationMetaData(
                userId, "TestUser", "pass", Role.USER, true
        );

        MockHttpServletRequestBuilder request = get("/stories/{id}", storyId)
                .with(user(principal));

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("story-details"))
                .andExpect(model().attributeExists("story"))
                .andExpect(model().attributeExists("storyLikes"))
                .andExpect(model().attributeExists("getLastThreeLikes"))
                .andExpect(model().attributeExists("editStoryRequest"))
                .andExpect(model().attributeExists("currentUserId"))
                .andExpect(model().attributeExists("username"))
                .andExpect(model().attributeExists("newComment"))
                .andExpect(model().attributeExists("comments"));
    }
    @Test
    void createStoryPage_shouldReturnCreateStoryViewWithUserAndRequestModel() throws Exception {
        // Given
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .username("testuser")
                .build();

        AuthenticationMetaData authMetaData = mock(AuthenticationMetaData.class);
        when(authMetaData.getId()).thenReturn(userId);

        when(userService.getById(userId)).thenReturn(user);

        Authentication authentication = new UsernamePasswordAuthenticationToken(authMetaData, null, List.of());

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Perform
        mockMvc.perform(get("/stories/create")
                        .with(authentication(authentication)))
                .andExpect(status().isOk())
                .andExpect(view().name("create-story"))
                .andExpect(model().attribute("user", user))
                .andExpect(model().attributeExists("createStoryRequest"));
    }


    @Test
    void initializeStory_shouldReturnCreateStoryViewWhenValidationFails() throws Exception {
        // Given
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .username("testuser")
                .build();

        AuthenticationMetaData authMetaData = mock(AuthenticationMetaData.class);
        when(authMetaData.getId()).thenReturn(userId);
        when(userService.getById(userId)).thenReturn(user);

        Authentication authentication = new UsernamePasswordAuthenticationToken(authMetaData, null, List.of());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        mockMvc.perform(post("/stories/create")
                        .with(authentication(authentication))
                        .param("title", "")
                        .param("content", "Sample content")
                        .with(csrf())
                )
                .andExpect(status().isOk())  // Expect 200 status (form should render with errors)
                .andExpect(view().name("create-story"))  // Expect the view to be 'create-story'
                .andExpect(model().attributeExists("user"))  // Ensure user object is present
                .andExpect(model().attributeExists("createStoryRequest"))  // Ensure createStoryRequest object is present
                .andExpect(model().hasErrors());  // Check if the form has errors (due to invalid title)
    }





}


