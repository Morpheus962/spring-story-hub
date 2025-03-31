package com.example.spring_story_hub.notification.client;

import com.example.spring_story_hub.notification.client.dto.Notification;
import com.example.spring_story_hub.notification.client.dto.NotificationPreference;
import com.example.spring_story_hub.notification.client.dto.NotificationRequest;
import com.example.spring_story_hub.notification.client.dto.UpsertNotificationPreference;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "notifications-service", url = "http://localhost:8081/api/v1/notifications")
public interface NotificationClient {

    @PostMapping("/preferences")
    ResponseEntity<Void> upsertNotificationPreference(@RequestBody UpsertNotificationPreference notificationPreference);

    @GetMapping("/preferences")
    ResponseEntity<NotificationPreference> getUserPreference(@RequestParam(name = "userId") UUID userId);

    @GetMapping
    ResponseEntity<List<Notification>> getNotificationHistory(@RequestParam(name = "userId")UUID userId);

    @PostMapping
    ResponseEntity<Void> sendNotification(@RequestBody NotificationRequest notificationRequest);

}
