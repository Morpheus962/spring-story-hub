package com.example.spring_story_hub.notification.service;

import com.example.spring_story_hub.notification.client.NotificationClient;
import com.example.spring_story_hub.notification.client.dto.Notification;
import com.example.spring_story_hub.notification.client.dto.NotificationPreference;
import com.example.spring_story_hub.notification.client.dto.NotificationRequest;
import com.example.spring_story_hub.notification.client.dto.UpsertNotificationPreference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class NotificationService {
    private final NotificationClient notificationClient;

    @Autowired
    public NotificationService(NotificationClient notificationClient) {
        this.notificationClient = notificationClient;
    }

    public void saveNotificationPreference(UUID userId, boolean isEmailEnabled, String email) {

        UpsertNotificationPreference notificationPreference = UpsertNotificationPreference.builder()
                .userId(userId)
                .contactInfo(email)
                .notificationEnabled(isEmailEnabled)
                .build();


        ResponseEntity<Void> httpResponse = notificationClient.upsertNotificationPreference(notificationPreference);
        if (!httpResponse.getStatusCode().is2xxSuccessful()) {
            log.error("[Feign call to notification-svc failed] Can't save user preference for user with id = [%s]".formatted(userId));
        }
    }

    public NotificationPreference getNotificationPreference(UUID userId) {

        ResponseEntity<NotificationPreference> httpResponse = notificationClient.getUserPreference(userId);

        if (!httpResponse.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Notification preference for user id [%s] does not exist.".formatted(userId));
        }

        return httpResponse.getBody();
    }
    public void sendNotification(UUID userId, String emailSubject, String emailBody) {

        NotificationRequest notificationRequest = NotificationRequest.builder()
                .userId(userId)
                .subject(emailSubject)
                .body(emailBody)
                .build();

        // Servive to Service
        ResponseEntity<Void> httpResponse;
        try {
            httpResponse = notificationClient.sendNotification(notificationRequest);
            if (!httpResponse.getStatusCode().is2xxSuccessful()) {
                log.error("[Feign call to notification-svc failed] Can't send email to user with id = [%s]".formatted(userId));
            }
        } catch (Exception e) {
            log.warn("Can't send email to user with id = [%s] due to 500 Internal Server Error.".formatted(userId));
        }
    }


    public List<Notification> getNotificationHistory(UUID userId) {

        ResponseEntity<List<Notification>> httpResponse = notificationClient.getNotificationHistory(userId);

        return httpResponse.getBody();
    }

}
