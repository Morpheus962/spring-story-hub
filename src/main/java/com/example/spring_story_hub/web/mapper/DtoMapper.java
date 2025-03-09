package com.example.spring_story_hub.web.mapper;

import com.example.spring_story_hub.user.models.User;
import com.example.spring_story_hub.web.dto.EditUserRequest;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DtoMapper {
    public static EditUserRequest mapToEditUserRequest(User user){
        return EditUserRequest.builder()
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .profilePicture(user.getProfilePicture())
                .build();
    }
}
