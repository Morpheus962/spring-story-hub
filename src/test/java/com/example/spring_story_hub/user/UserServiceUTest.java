package com.example.spring_story_hub.user;

import com.example.spring_story_hub.exception.DomainException;
import com.example.spring_story_hub.exception.UsernameAlreadyExistException;
import com.example.spring_story_hub.notification.service.NotificationService;
import com.example.spring_story_hub.security.AuthenticationMetaData;
import com.example.spring_story_hub.user.models.Role;
import com.example.spring_story_hub.user.models.User;
import com.example.spring_story_hub.user.repository.UserRepository;
import com.example.spring_story_hub.user.service.UserService;
import com.example.spring_story_hub.web.dto.EditUserRequest;
import com.example.spring_story_hub.web.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceUTest {

    @Mock
    private  UserRepository userRepository;
    @Mock
    private  PasswordEncoder passwordEncoder;
    @Mock
    private  NotificationService notificationService;
    @InjectMocks
    private UserService userService;


    @Test
    void givenExistingUsername_whenRegister_thenExceptionIsThrown(){
        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("Vik123")
                .password("123123")
                .email("vik@email.bg")
                .build();

        when(userRepository.findByUsername(any())).thenReturn(Optional.of(new User()));

        assertThrows(UsernameAlreadyExistException.class, () -> userService.register(registerRequest));
        verify(userRepository, never()).save(any());
        verify(notificationService, never()).saveNotificationPreference(any(UUID.class), anyBoolean(), anyString());

    }

    @Test
    void givenHappyPath_thenRegister(){
        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("dido123")
                .password("123123")
                .email("dido@email.bg")
                .build();

        UUID userId = UUID.randomUUID();

        User savedUser = User.builder()
                .id(userId)
                .build();

        when(userRepository.findByUsername(registerRequest.getUsername())).thenReturn(Optional.empty());
        when(userRepository.save(any())).thenAnswer(invocation -> {
            User userToSave = invocation.getArgument(0);
            userToSave.setId(userId); // simulate DB auto-generating the ID
            return userToSave;
        });
        User registeredUser = userService.register(registerRequest);

        assertEquals(registeredUser.getUsername(), registerRequest.getUsername());
        verify(notificationService, times(1)).saveNotificationPreference(savedUser.getId(), false, null);
    }

    @Test
    void givenMissingUserFromDatabase_whenEditUserDetails_thenExceptionIsThrown(){
        UUID userId = UUID.randomUUID();
        EditUserRequest dto = EditUserRequest.builder().build();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(NullPointerException.class, () -> userService.editUserDetails(userId, dto));

    }

    @Test
    void givenExistingUser_whenEditUserDetailsWithActualEmail_thenChangeTheirDetailsSaveNotificationPreferenceAndSaveToDatabase(){
        UUID userId = UUID.randomUUID();
        EditUserRequest dto = EditUserRequest.builder()
                .username("ivan123")
                .email("ivan123@email.bg")
                .firstName("Ivan")
                .lastName("Ivanov")
                .profilePicture("www.profilepic.com")
                .build();
        User user = User.builder().build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.editUserDetails(userId, dto);
        assertEquals("Ivan", user.getFirstName());
        assertEquals("Ivanov", user.getLastName());
        assertEquals("ivan123@email.bg", user.getEmail());
        assertEquals("www.profilepic.com", user.getProfilePicture());
        verify(notificationService, times(1)).saveNotificationPreference(userId, true, dto.getEmail());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void givenExistingUser_whenEditUserDetailsWithEmptyEmail_thenChangeTheirDetailsSaveNotificationPreferenceAndSaveToDatabase(){
        UUID userId = UUID.randomUUID();
        EditUserRequest dto = EditUserRequest.builder()
                .username("ivan123")
                .email("")
                .firstName("Ivan")
                .lastName("Ivanov")
                .profilePicture("www.profilepic.com")
                .build();
        User user = User.builder().build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.editUserDetails(userId, dto);
        assertEquals("Ivan", user.getFirstName());
        assertEquals("Ivanov", user.getLastName());
        assertEquals("", user.getEmail());
        assertEquals("www.profilepic.com", user.getProfilePicture());
        verify(notificationService, times(1)).saveNotificationPreference(userId, false, null);
        verify(userRepository, times(1)).save(user);

    }
    @Test
    void givenUserWithRoleUser_WhenSwitchRole_thenUserReceivesAdministratorRole(){
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .role(Role.USER)
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        userService.switchRole(userId);
        assertEquals(Role.ADMINISTRATOR, user.getRole());
    }
    @Test
    void givenUserWithRoleAdministrator_WhenSwitchRole_thenUserReceivesUserRole(){
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .role(Role.ADMINISTRATOR)
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        userService.switchRole(userId);
        assertEquals(Role.USER, user.getRole());
    }

    @Test
    void givingMissingUser_whenLoadUserByUsername_thenReturnException(){
        String username = "ivan123";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        assertThrows(NullPointerException.class, () -> userService.loadUserByUsername(username));
    }
    @Test
    void givingExistingUser_whenLoadUserByUsername_thenReturnCorrectAuthenticationMetaData(){
        String username = "ivan123";
        User user = User.builder()
                .id(UUID.randomUUID())
                .isActive(true)
                .password("123123")
                .role(Role.USER)
                .build();
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        UserDetails authenticationMetaData = userService.loadUserByUsername(username);
        assertInstanceOf(AuthenticationMetaData.class, authenticationMetaData);
        AuthenticationMetaData result = (AuthenticationMetaData) authenticationMetaData;
        assertEquals(username, result.getUsername());
        assertEquals(user.getPassword(), result.getPassword());
        assertEquals(user.getRole(), result.getRole());
        assertEquals("ROLE_USER", result.getAuthorities().iterator().next().getAuthority());

    }

    @Test
    void givenExistingUsersInDatabase_whenGetAllUsers_thenReturnThemAll() {

        List<User> userList = List.of(new User(), new User());
        when(userRepository.findAll()).thenReturn(userList);

        List<User> users = userService.getAllUsers();

        assertEquals(2, users.size());
    }

}
