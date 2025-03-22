package com.example.spring_story_hub.web.controller;

import com.example.spring_story_hub.comment.models.Comment;
import com.example.spring_story_hub.comment.service.CommentService;
import com.example.spring_story_hub.security.AuthenticationMetaData;
import com.example.spring_story_hub.user.models.User;
import com.example.spring_story_hub.user.service.UserService;
import com.example.spring_story_hub.web.dto.CommentRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;
    private final UserService userService;
    @Autowired
    public CommentController(CommentService commentService, UserService userService) {
        this.commentService = commentService;
        this.userService = userService;
    }

    @GetMapping("/story/{id}")
    public ModelAndView getCommentsPage(@PathVariable UUID id){
        List<Comment> comments = commentService.getCommentsByStory(id);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("story-details");
        modelAndView.addObject("comments", comments);
        modelAndView.addObject("commentRequest", new CommentRequest());
        return modelAndView;
    }

    @PostMapping("/story/{id}")
    public ModelAndView createComment(@PathVariable UUID id, @Valid CommentRequest commentRequest, BindingResult bindingResult, @AuthenticationPrincipal AuthenticationMetaData authenticationMetaData){
        UUID userId = authenticationMetaData.getId();
        User user = userService.getById(userId);
        if (bindingResult.hasErrors()) {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("create-story");
            modelAndView.addObject("user", user);
            modelAndView.addObject("commentRequest", commentRequest);
            return modelAndView;
        }
        Comment comment = commentService.createComment(user, commentRequest, id);
        return new ModelAndView("redirect:/stories/" + id);
    }
}
