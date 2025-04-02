package com.example.spring_story_hub.web.controller;

import com.example.spring_story_hub.notification.client.dto.Notification;
import com.example.spring_story_hub.notification.client.dto.NotificationPreference;
import com.example.spring_story_hub.notification.service.NotificationService;
import com.example.spring_story_hub.security.AuthenticationMetaData;
import com.example.spring_story_hub.user.models.User;
import com.example.spring_story_hub.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/notifications")
public class NotificationController {
    private final UserService userService;
    private final NotificationService notificationService;

    @Autowired
    public NotificationController(UserService userService, NotificationService notificationService) {
        this.userService = userService;
        this.notificationService = notificationService;
    }

    @GetMapping
    private ModelAndView getNotificationsPage(@AuthenticationPrincipal AuthenticationMetaData authenticationMetaData){
        User user = userService.getById(authenticationMetaData.getId());
        NotificationPreference notificationPreference = notificationService.getNotificationPreference(user.getId());
        List<Notification> notificationHistory = notificationService.getNotificationHistory(authenticationMetaData.getId());
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("notifications");
        modelAndView.addObject("user", user);
        modelAndView.addObject("notificationPreference", notificationPreference);
        modelAndView.addObject("notificationHistory", notificationHistory);
        return modelAndView;
    }

    @PutMapping("/user-preference")
    public String updateUserPreference(@RequestParam(name = "enabled") boolean enabled, @AuthenticationPrincipal AuthenticationMetaData authenticationMetaData) {

        notificationService.updateNotificationPreference(authenticationMetaData.getId(), enabled);

        return "redirect:/notifications";
    }
}
