package com.example.spring_story_hub.web.controller;

import com.example.spring_story_hub.comment.models.Comment;
import com.example.spring_story_hub.comment.service.CommentService;
import com.example.spring_story_hub.like.models.StoryLike;
import com.example.spring_story_hub.report.service.ReportService;
import com.example.spring_story_hub.scheduler.StoryScheduler;
import com.example.spring_story_hub.security.AuthenticationMetaData;
import com.example.spring_story_hub.story.models.Story;
import com.example.spring_story_hub.story.service.StoryService;
import com.example.spring_story_hub.user.models.Role;
import com.example.spring_story_hub.user.models.User;
import com.example.spring_story_hub.user.service.UserService;
import com.example.spring_story_hub.web.dto.CommentRequest;
import com.example.spring_story_hub.web.dto.CreateReportRequest;
import com.example.spring_story_hub.web.dto.CreateStoryRequest;
import com.example.spring_story_hub.web.dto.EditStoryRequest;
import com.example.spring_story_hub.web.mapper.DtoMapper;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@Slf4j
@RequestMapping("/stories")
public class StoryController {
    private final StoryService storyService;
    private final UserService userService;
    private final ReportService reportService;
    private final CommentService commentService;
    private final StoryScheduler storyScheduler;

    @Autowired
    public StoryController(StoryService storyService, UserService userService, ReportService reportService, CommentService commentService, StoryScheduler storyScheduler) {
        this.storyService = storyService;
        this.userService = userService;

        this.reportService = reportService;
        this.commentService = commentService;
        this.storyScheduler = storyScheduler;
    }


    @GetMapping
    public ModelAndView getStoriesPage() {
        List<Story> stories = storyService.findAllStories();
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("stories");
        modelAndView.addObject("stories", stories);
        return modelAndView;
    }

    @GetMapping("/{id}")
    public ModelAndView readStory(@PathVariable UUID id, @AuthenticationPrincipal AuthenticationMetaData authenticationMetaData) {
        Story story = storyService.findById(id);
        long storyLikes = story.getStoryLikes().stream().count();
        String username = authenticationMetaData.getUsername();
        List<StoryLike> getLastThreeLikes = story.getStoryLikes().stream().limit(3).toList();
        List<Comment> comments = commentService.getCommentsByStory(id);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("story-details");
        modelAndView.addObject("story", story);
        modelAndView.addObject("storyLikes", storyLikes);
        modelAndView.addObject("getLastThreeLikes", getLastThreeLikes);
        modelAndView.addObject("editStoryRequest", DtoMapper.mapToStoryRequest(story));
        modelAndView.addObject("currentUserId", authenticationMetaData.getId());
        modelAndView.addObject("username", username);
        modelAndView.addObject("newComment", new CommentRequest());
        modelAndView.addObject("comments", comments);
        return modelAndView;

    }


    @GetMapping("/create")
    public ModelAndView createStoryPage(@AuthenticationPrincipal AuthenticationMetaData authenticationMetaData) {
        UUID userId = authenticationMetaData.getId();
        User user = userService.getById(userId);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("create-story");
        modelAndView.addObject("user", user);
        modelAndView.addObject("createStoryRequest", new CreateStoryRequest());
        return modelAndView;
    }

    @PostMapping("/create")
    public ModelAndView initializeStory(@Valid CreateStoryRequest createStoryRequest, BindingResult bindingResult, @AuthenticationPrincipal AuthenticationMetaData authenticationMetaData) {
        UUID userId = authenticationMetaData.getId();
        User user = userService.getById(userId);

        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("create-story");
            modelAndView.addObject("user", user);
            modelAndView.addObject("createStoryRequest", createStoryRequest);
            return modelAndView;
        }

        Story story = storyService.createStory(user, createStoryRequest);
        return new ModelAndView("redirect:/stories/" + story.getId());
    }
    @GetMapping("/{id}/edit")
    public ModelAndView editStoryPage(@PathVariable UUID id, @AuthenticationPrincipal AuthenticationMetaData authenticationMetaData) {
        // Retrieve the story by its ID
        Story story = storyService.findById(id);

        // Ensure the current user has permission to edit this story
        if (!story.getOwner().getId().equals(authenticationMetaData.getId())) {
            // You can redirect or return an error view if the user doesn't have permission
            return new ModelAndView("redirect:/stories");
        }

        // Prepare the model and view
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("edit-story");
        modelAndView.addObject("story", story);
        modelAndView.addObject("editStoryRequest", DtoMapper.mapToStoryRequest(story));  // Map the story to your DTO
        return modelAndView;
    }

    @PutMapping("/{id}/edit")
    public ModelAndView editUserStory(@PathVariable UUID id, @Valid EditStoryRequest editStoryRequest, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            Story story = storyService.getById(id);
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("edit-story");
            modelAndView.addObject("story", story);
            modelAndView.addObject("editStoryRequest", editStoryRequest);
            return modelAndView;
        }

        storyService.editStory(id, editStoryRequest);
        return new ModelAndView("redirect:/stories/" + id);
    }


    @GetMapping("/{id}/report")
    public ModelAndView createReportPage(@PathVariable UUID id, @AuthenticationPrincipal AuthenticationMetaData authenticationMetaData){
        UUID userId = authenticationMetaData.getId();
        User user = userService.getById(userId);
        Story story = storyService.getById(id);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("create-report");
        modelAndView.addObject("user", user);
        modelAndView.addObject("story", story);
        modelAndView.addObject("createReportRequest", new CreateReportRequest());
        return modelAndView;

    }

    @GetMapping("/top")
    public ModelAndView showTopStories(@AuthenticationPrincipal AuthenticationMetaData authenticationMetaData){
        List<Story> topStories = storyScheduler.getCachedTopStories();
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("top-stories");
        modelAndView.addObject("topStories", topStories);
        return modelAndView;

    }
    @PostMapping("/{id}/report")
    public String createReport(@PathVariable UUID id, @AuthenticationPrincipal AuthenticationMetaData authenticationMetaData, @Valid CreateReportRequest createReportRequest, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return "create-report";
        }
        Story story = storyService.findById(id);
        UUID userId = authenticationMetaData.getId();
        reportService.createReport(id, userId, createReportRequest);

        return "redirect:/stories/" + id + "?reportSuccess=true";
    }

    @PostMapping("/{id}/like")
    public String likeStory(@PathVariable UUID id, @AuthenticationPrincipal AuthenticationMetaData authenticationMetaData){
        UUID userId = authenticationMetaData.getId();
        storyService.likeStory(id, userId);

        return "redirect:/stories/" + id;

    }
}
