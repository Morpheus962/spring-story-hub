package com.example.spring_story_hub.comment;

import com.example.spring_story_hub.comment.models.Comment;
import com.example.spring_story_hub.comment.repository.CommentRepository;
import com.example.spring_story_hub.comment.service.CommentService;
import com.example.spring_story_hub.story.models.Story;
import com.example.spring_story_hub.story.service.StoryService;
import com.example.spring_story_hub.user.models.User;
import com.example.spring_story_hub.web.dto.CommentRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentServiceUTest {
    @Mock
    private  CommentRepository commentRepository;
    @Mock
    private  StoryService storyService;
    @InjectMocks
    private CommentService commentService;
    @Test
    void givenHappyPath_whenCreateComment(){
        UUID storyId = UUID.randomUUID();
        User user = User.builder()
                .id(UUID.randomUUID())
                .build();
        CommentRequest commentRequest = new CommentRequest();
        commentRequest.setContent("test");
        Story mockStory = Story.builder().id(storyId).build();
        when(storyService.getById(storyId)).thenReturn(mockStory);
        Comment comment = Comment.builder()
                .id(UUID.randomUUID())
                .createdOn(LocalDateTime.now())
                .owner(user)
                .content(commentRequest.getContent())
                .story(mockStory)
                .build();
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        Comment result = commentService.createComment(user, commentRequest, storyId);
        assertEquals(user, result.getOwner());
        assertEquals(mockStory, result.getStory());
        assertEquals(commentRequest.getContent(), result.getContent());
        verify(commentRepository, times(1)).save(argThat(savedComment ->
                savedComment.getOwner().equals(user) &&
                        savedComment.getStory().equals(mockStory) &&
                        savedComment.getContent().equals(commentRequest.getContent())
        ));
    }

    @Test
    void givenStoryId_whenGetCommentsByStory_thenReturnsComments(){
        UUID storyId = UUID.randomUUID();
        Comment comment1 = Comment.builder()
                .id(UUID.randomUUID())
                .content("Comment 1")
                .story(Story.builder().id(storyId).build())
                .build();

        Comment comment2 = Comment.builder()
                .id(UUID.randomUUID())
                .content("Comment 2")
                .story(Story.builder().id(storyId).build())
                .build();
        when(commentRepository.findByStoryId(storyId)).thenReturn(Arrays.asList(comment1, comment2));
        List<Comment> comments = commentService.getCommentsByStory(storyId);
        assertEquals(2, comments.size(), "Should return 2 comments");
        assertEquals("Comment 1", comments.get(0).getContent());
        assertEquals("Comment 2", comments.get(1).getContent());
        verify(commentRepository, times(1)).findByStoryId(storyId);


    }
}
