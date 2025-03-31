package com.example.spring_story_hub.notification.client.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class NotificationPreference {

    private UUID id;

    private UUID userId;

    private boolean enabled;

    private String contactInfo;

}
