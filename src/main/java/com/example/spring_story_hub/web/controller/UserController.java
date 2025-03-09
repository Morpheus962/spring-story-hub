package com.example.spring_story_hub.web.controller;

import com.example.spring_story_hub.user.models.User;
import com.example.spring_story_hub.user.service.UserService;
import com.example.spring_story_hub.web.dto.EditUserRequest;
import com.example.spring_story_hub.web.mapper.DtoMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Controller
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}/profile")
    public ModelAndView getProfilePage(@PathVariable UUID id) {
        User user = userService.getById(id);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("profile-menu");
        modelAndView.addObject("user", user);
        modelAndView.addObject("editUserRequest", DtoMapper.mapToEditUserRequest(user));
        return modelAndView;

    }

    @PutMapping("/{id}/profile")
    public ModelAndView updateUserProfile(@PathVariable UUID id, @Valid EditUserRequest editUserRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            User user = userService.getById(id);
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("profile-menu");
            modelAndView.addObject("user", user);
            modelAndView.addObject("editUserRequest", editUserRequest);
            return modelAndView;
        }
        userService.editUserDetails(id, editUserRequest);
        return new ModelAndView("redirect:/users/" + id + "/profile");

    }
}