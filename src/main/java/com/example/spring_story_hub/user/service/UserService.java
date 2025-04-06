package com.example.spring_story_hub.user.service;

import ch.qos.logback.core.model.Model;
import com.example.spring_story_hub.exception.UsernameAlreadyExistException;
import com.example.spring_story_hub.notification.service.NotificationService;
import com.example.spring_story_hub.security.AuthenticationMetaData;
import com.example.spring_story_hub.user.models.Role;
import com.example.spring_story_hub.user.models.User;
import com.example.spring_story_hub.user.repository.UserRepository;
import com.example.spring_story_hub.web.dto.EditUserRequest;
import com.example.spring_story_hub.web.dto.RegisterRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final NotificationService notificationService;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, NotificationService notificationService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.notificationService = notificationService;
    }


    public User register(RegisterRequest registerRequest) {
        Optional<User> optionalUser = userRepository.findByUsername(registerRequest.getUsername());

        if (optionalUser.isPresent()){
            throw new UsernameAlreadyExistException("Username %s is already taken.".formatted(registerRequest));
        }
        User user = initializeUser(registerRequest);

        userRepository.save(user);
        log.info("Successfully created user %s with id %s".formatted(user.getUsername(), user.getId()));
        notificationService.saveNotificationPreference(user.getId(), false, null);

        log.info("Successfully create new user account for username [%s] and id [%s]".formatted(user.getUsername(), user.getId()));

        return user;
    }

    private User initializeUser(RegisterRequest registerRequest) {
        return User.builder()
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(Role.USER)
                .isActive(true)
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();
    }

    public User getById(UUID userId) {
        return userRepository.findById(userId).orElseThrow(()
                -> new NullPointerException("User with id [%s] does not exist.".formatted(userId)));

    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new NullPointerException("User with this username does not exist."));


        return new AuthenticationMetaData(user.getId(), username, user.getPassword(), user.getRole(), user.isActive());

    }

    public void editUserDetails(UUID id, EditUserRequest editUserRequest) {
        User user = getById(id);
        if (editUserRequest.getEmail().isBlank()) {
            notificationService.saveNotificationPreference(id, false, null);
        }

        user.setFirstName(editUserRequest.getFirstName());
        user.setLastName(editUserRequest.getLastName());
        user.setEmail(editUserRequest.getEmail());
        user.setProfilePicture(editUserRequest.getProfilePicture());

        if (!editUserRequest.getEmail().isBlank()) {
            notificationService.saveNotificationPreference(id, true, editUserRequest.getEmail());
        }


        userRepository.save(user);
    }

    @Cacheable("users")
    public List<User> getAllUsers() {

        return userRepository.findAll();
    }


    @CacheEvict(value = "users", allEntries = true)
    public void switchRole(UUID userId) {

        User user = getById(userId);

        if (user.getRole() == Role.USER) {
            user.setRole(Role.ADMINISTRATOR);
        } else {
            user.setRole(Role.USER);
        }

        userRepository.save(user);
    }

}
