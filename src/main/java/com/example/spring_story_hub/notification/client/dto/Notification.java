package com.example.spring_story_hub.notification.client.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Notification {

    private String subject;

    private LocalDateTime createdOn;

    private String status;
}
