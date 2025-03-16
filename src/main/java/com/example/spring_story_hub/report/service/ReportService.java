package com.example.spring_story_hub.report.service;

import com.example.spring_story_hub.report.models.Report;
import com.example.spring_story_hub.report.repository.ReportRepository;
import com.example.spring_story_hub.story.models.Story;
import com.example.spring_story_hub.story.service.StoryService;
import com.example.spring_story_hub.user.models.User;
import com.example.spring_story_hub.user.service.UserService;
import com.example.spring_story_hub.web.dto.CreateReportRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ReportService {
    private final ReportRepository reportRepository;
    private final UserService userService;
    private final StoryService storyService;

    @Autowired
    public ReportService(ReportRepository reportRepository, UserService userService, StoryService storyService) {
        this.reportRepository = reportRepository;
        this.userService = userService;
        this.storyService = storyService;
    }

    public List<Report> getAllReports() {

        return reportRepository.findAll();
    }

    public void createReport(UUID id, UUID userId, CreateReportRequest createReportRequest) {
        User user = userService.getById(userId);
        Story story = storyService.getById(id);
        Report report = Report.builder()
                .reason(createReportRequest.getReason())
                .reporter(user)
                .createdOn(LocalDateTime.now())
                .reportedStory(story)
                .build();
        reportRepository.save(report);

    }

}
