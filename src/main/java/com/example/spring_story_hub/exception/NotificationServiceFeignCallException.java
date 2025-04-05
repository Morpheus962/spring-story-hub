package com.example.spring_story_hub.exception;

public class NotificationServiceFeignCallException extends RuntimeException{
    public NotificationServiceFeignCallException() {
    }

    public NotificationServiceFeignCallException(String message) {
        super(message);
    }
}
