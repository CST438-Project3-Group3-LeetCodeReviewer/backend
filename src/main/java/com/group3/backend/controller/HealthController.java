package com.group3.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.group3.backend.repository.FeedbackRepository;

@RestController
public class HealthController {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @GetMapping("/api/health")
    public String health() {
        return "Backend is running";
    }

    @GetMapping("/api/health/db")
    public String dbHealth() {
        try {
            feedbackRepository.count();
            return "Database connection successful";
        } catch (Exception e) {
            return "Database connection failed: " + e.getMessage();
        }
    }
}