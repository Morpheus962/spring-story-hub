package com.example.spring_story_hub.web.controller;

import com.example.spring_story_hub.report.models.Report;
import com.example.spring_story_hub.report.service.ReportService;
import com.example.spring_story_hub.security.AuthenticationMetaData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/admin/reports")
public class ReportController {

    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }


    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ModelAndView getAllReports(@AuthenticationPrincipal AuthenticationMetaData authenticationMetaData){
        List<Report> reports = reportService.getAllReports();
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("reports");
        modelAndView.addObject("reports", reports);
        return modelAndView;
    }
}
