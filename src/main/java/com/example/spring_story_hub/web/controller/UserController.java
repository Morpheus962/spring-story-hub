package com.example.spring_story_hub.web.controller;

import com.example.spring_story_hub.security.AuthenticationMetaData;
import com.example.spring_story_hub.user.models.Role;
import com.example.spring_story_hub.user.models.User;
import com.example.spring_story_hub.user.service.UserService;
import com.example.spring_story_hub.web.dto.EditUserRequest;
import com.example.spring_story_hub.web.mapper.DtoMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ModelAndView getAllUsers(@AuthenticationPrincipal AuthenticationMetaData authenticationMetadata) {

        List<User> users = userService.getAllUsers();

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("users");
        modelAndView.addObject("users", users);
        modelAndView.addObject("role", authenticationMetadata.getRole());

        return modelAndView;
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



    @PutMapping("/{id}/role")// PUT /users/{id}/role
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public String switchUserRole(@PathVariable UUID id) {

        userService.switchRole(id);

        return "redirect:/users";
    }

}