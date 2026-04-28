package com.group3.backend.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.group3.backend.entity.Feedback;
import com.group3.backend.repository.FeedbackRepository;

@RestController
@RequestMapping("/submissions")
public class FeedbackController {

    @Autowired
    private FeedbackRepository feedbackRepository;

    /**
     * TEMP endpoint (for testing only)
     * Later this should be created automatically after submission
     */
    @PostMapping("/{id}/feedback")
    public Feedback createFeedback(@PathVariable Long id, @RequestBody Feedback feedback) {
        feedback.setSubmissionId(id);
        feedback.setCreatedAt(LocalDateTime.now());

        return feedbackRepository.save(feedback);
    }

    /**
     * Get feedback by USER (UUID FIXED)
     */
    @GetMapping("/feedback/user/{userId}")
    public List<Feedback> getUserFeedback(@PathVariable UUID userId) {
        return feedbackRepository.findByUserId(userId);
    }

    /**
     * Get feedback for ONE submission (MAIN ENDPOINT your UI will use)
     */
    @GetMapping("/{id}/feedback")
    public Feedback getSubmissionFeedback(@PathVariable Long id) {
        return feedbackRepository.findBySubmissionId(id)
                .orElseThrow(() -> new RuntimeException("Feedback not found"));
    }
}