package com.group3.backend.controller;

import com.group3.backend.entity.Feedback;
import com.group3.backend.repository.FeedbackRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/submissions")
public class FeedbackController {

    @Autowired
    private FeedbackRepository feedbackRepository;

    //  CREATE feedback for a submission
    @PostMapping("/{submissionId}/feedback")
    public Feedback createFeedback(
            @PathVariable Long submissionId,
            @RequestBody Feedback feedback) {

        if (feedback.getUserId() == null) {
            throw new RuntimeException("userId is required");
        }

        feedback.setSubmissionId(submissionId);
        feedback.setCreatedAt(LocalDateTime.now());

        return feedbackRepository.save(feedback);
    }

    //  GET feedback by submission
    @GetMapping("/{submissionId}/feedback")
    public Feedback getFeedbackBySubmission(@PathVariable Long submissionId) {
        return feedbackRepository
                .findBySubmissionId(submissionId)
                .orElse(null);
    }

    //  GET feedback by user (UUID)
    @GetMapping("/user/{userId}")
    public List<Feedback> getFeedbackByUser(@PathVariable UUID userId) {
        List<Feedback> feedbackList = feedbackRepository.findByUserId(userId);

        if (feedbackList.isEmpty()) {
            throw new RuntimeException("No feedback found for user");
        }

        return feedbackList;
    }
}