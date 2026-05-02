package com.group3.backend.controller;

import com.group3.backend.repository.FeedbackRepository;
import java.util.UUID;

import com.group3.backend.entity.Feedback;
import com.group3.backend.entity.Submission;
import com.group3.backend.repository.SubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/submissions")
public class SubmissionController {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private SubmissionRepository submissionRepository;

    @PostMapping
    public Submission createSubmission(@RequestBody Submission submission) {
        if (submission.getProblemId() == null || submission.getUserId() == null) {
            throw new IllegalArgumentException("Problem ID and User ID are required.");
        }

        submission.setCreatedAt(LocalDateTime.now());
        Submission savedSubmission = submissionRepository.save(submission);

        // Generate and save automated feedback
        Feedback feedback = new Feedback();
        feedback.setSubmissionId(savedSubmission.getId());
        // Since Feedback expects a UUID for userId, we check if we can map it
        // For now, we use a placeholder UUID or the user's ID if applicable
        feedback.setUserId(UUID.randomUUID()); 
        feedback.setFeedbackText(generateMockFeedback(savedSubmission.getCode()));
        feedback.setScore(generateMockScore());
        feedback.setCreatedAt(LocalDateTime.now());
        feedbackRepository.save(feedback);

        return savedSubmission;
    }

    private String generateMockFeedback(String code) {
        if (code == null || code.length() < 20) {
            return "Your solution is too short. Make sure you fully implement the logic.";
        } else if (code.contains("for") || code.contains("while")) {
            return "Good use of loops. Consider optimizing time complexity if possible.";
        } else {
            return "Clean structure, but consider edge cases and input validation.";
        }
    }

    private int generateMockScore() {
        return (int)(Math.random() * 40) + 60; // 60–100
    }

    @GetMapping("/user/{userId}")
    public List<Submission> getUserSubmissions(@PathVariable Long userId) {
        return submissionRepository.findByUserId(userId);
    }
}
