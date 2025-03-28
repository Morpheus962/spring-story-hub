package com.example.spring_story_hub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SpringStoryHubApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringStoryHubApplication.class, args);
	}

}
