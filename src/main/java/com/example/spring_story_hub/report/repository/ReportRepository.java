package com.example.spring_story_hub.report.repository;

import com.example.spring_story_hub.report.models.Report;
import com.example.spring_story_hub.story.models.Story;
import com.example.spring_story_hub.user.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReportRepository extends JpaRepository<Report, UUID> {
    Optional<Report> findByReportedStoryAndReporter(Story reportedStory, User reporter);
}
