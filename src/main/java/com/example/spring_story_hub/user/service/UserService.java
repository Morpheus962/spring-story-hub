package com.example.spring_story_hub.user.service;

import ch.qos.logback.core.model.Model;
import com.example.spring_story_hub.user.models.Role;
import com.example.spring_story_hub.user.models.User;
import com.example.spring_story_hub.user.repository.UserRepository;
import com.example.spring_story_hub.web.dto.RegisterRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;
@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    public User register(RegisterRequest registerRequest) {
        Optional<User> optionalUser = userRepository.findByUsername(registerRequest.getUsername());

        if (optionalUser.isPresent()){
            throw new IllegalArgumentException("Username %s is already taken.".formatted(registerRequest));
        }
        User user = initializeUser(registerRequest);
        //stories, comments, storylikes, reports;
        userRepository.save(user);
        log.info("Successfully created user %s with id %s".formatted(user.getUsername(), user.getId()));
        return user;
    }

    private User initializeUser(RegisterRequest registerRequest) {
        return User.builder()
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(Role.USER)
                .build();
    }
}
