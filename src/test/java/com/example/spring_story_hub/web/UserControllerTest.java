package com.example.spring_story_hub.web;

import com.example.spring_story_hub.security.AuthenticationMetaData;
import com.example.spring_story_hub.user.models.Role;
import com.example.spring_story_hub.user.models.User;
import com.example.spring_story_hub.user.service.UserService;
import com.example.spring_story_hub.web.controller.UserController;
import com.example.spring_story_hub.web.dto.EditUserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private UUID userId;
    private User testUser;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        testUser = new User();
        testUser.setId(userId);
        testUser.setUsername("testUser");
        testUser.setRole(Role.USER);
    }

    @Test
    void getAllUsers_authenticatedAdmin_returnsUserList() throws Exception {
        AuthenticationMetaData principal = new AuthenticationMetaData(userId, "admin", "pass", Role.ADMINISTRATOR, true);

        when(userService.getAllUsers()).thenReturn(Collections.singletonList(testUser));

        mockMvc.perform(get("/users").with(user(principal)))
                .andExpect(status().isOk())
                .andExpect(view().name("users"))
                .andExpect(model().attributeExists("users"))
                .andExpect(model().attributeExists("role"));

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void getProfilePage_existingUser_returnsProfileMenu() throws Exception {
        when(userService.getById(userId)).thenReturn(testUser);

        mockMvc.perform(get("/users/" + userId + "/profile")
                        .with(user("someUser").roles("USER"))) // ← simulate login
                .andExpect(status().isOk())
                .andExpect(view().name("profile-menu"))
                .andExpect(model().attributeExists("user"));

        verify(userService, times(1)).getById(userId);
    }


    @Test
    void updateUserProfile_validRequest_redirectsToProfile() throws Exception {
        when(userService.getById(userId)).thenReturn(testUser);

        mockMvc.perform(put("/users/" + userId + "/profile")
                        .param("username", "updatedUser")
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("email", "test@example.com")
                        .param("profilePicture", "https://example.com/image.jpg")
                        .with(csrf()) // ✅ CSRF token for PUT
                        .with(user("testUser").roles("USER"))) // ✅ simulate an authenticated user
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users/" + userId + "/profile"));

        verify(userService, times(1)).editUserDetails(eq(userId), any(EditUserRequest.class));
    }




    @Test
    void updateUserProfile_invalidRequest_returnsFormWithErrors() throws Exception {
        // given
        when(userService.getById(userId)).thenReturn(testUser);

        // malformed email + overly long names + bad URL
        mockMvc.perform(put("/users/" + userId + "/profile")
                        .param("username", "johnny") // valid
                        .param("firstName", "a".repeat(25)) // too long
                        .param("lastName", "b".repeat(30)) // too long
                        .param("email", "not-an-email") // invalid email
                        .param("profilePicture", "not-a-url") // invalid URL
                        .with(csrf())
                        .with(user("testUser").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(view().name("profile-menu"))
                .andExpect(model().attributeExists("editUserRequest"))
                .andExpect(model().attributeHasFieldErrors("editUserRequest", "firstName", "lastName", "email", "profilePicture"));

        verify(userService, never()).editUserDetails(any(), any());
    }




    @Test
    void switchUserRole_asAdmin_redirectsToUsers() throws Exception {
        mockMvc.perform(put("/users/" + userId + "/role")
                        .with(csrf())  // Required for PUT
                        .with(user("adminUser").roles("ADMINISTRATOR")))  // Simulate an admin
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"));

        verify(userService, times(1)).switchRole(userId);
    }


}
