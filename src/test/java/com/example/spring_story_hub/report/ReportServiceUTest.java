package com.example.spring_story_hub.report;

import com.example.spring_story_hub.report.models.Report;
import com.example.spring_story_hub.report.repository.ReportRepository;
import com.example.spring_story_hub.report.service.ReportService;
import com.example.spring_story_hub.story.models.Story;
import com.example.spring_story_hub.story.service.StoryService;
import com.example.spring_story_hub.user.models.User;
import com.example.spring_story_hub.user.service.UserService;
import com.example.spring_story_hub.web.dto.CreateReportRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReportServiceUTest {
    @Mock
    private ReportRepository reportRepository;
    @Mock
    private UserService userService;
    @Mock
    private StoryService storyService;

    @InjectMocks
    private ReportService reportService;

    @Test
    void givenValidReport_whenCreateReport_thenSaveReport() {
        UUID userId = UUID.randomUUID();
        UUID storyId = UUID.randomUUID();

        User user = User.builder()
                .id(userId)
                .build();
        Story story = Story.builder()
                .id(storyId)
                .owner(user)
                .build();

        CreateReportRequest createReportRequest = new CreateReportRequest();
        createReportRequest.setReason("test reason");

        Report expectedReport = Report.builder()
                .reportedStory(story)
                .reporter(user)
                .reason(createReportRequest.getReason())
                .createdOn(LocalDateTime.now())
                .build();

        when(userService.getById(userId)).thenReturn(user);
        when(storyService.getById(storyId)).thenReturn(story);

        reportService.createReport(storyId, userId, createReportRequest);

        verify(reportRepository, times(1)).save(any(Report.class));

        ArgumentCaptor<Report> reportCaptor = ArgumentCaptor.forClass(Report.class);
        verify(reportRepository).save(reportCaptor.capture());

        Report capturedReport = reportCaptor.getValue();

        assertEquals(expectedReport.getReason(), capturedReport.getReason());
        assertEquals(expectedReport.getReporter().getId(), capturedReport.getReporter().getId());
        assertEquals(expectedReport.getReportedStory().getId(), capturedReport.getReportedStory().getId());
        assertNotNull(capturedReport.getCreatedOn());
    }
}
