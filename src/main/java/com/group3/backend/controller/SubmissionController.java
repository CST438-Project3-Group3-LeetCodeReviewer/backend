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

    // @PostMapping
    // public Submission createSubmission(@RequestBody Submission submission) {
    //     submission.setCreatedAt(LocalDateTime.now());
    //     return submissionRepository.save(submission);
    // }
    @PostMapping
    public Submission createSubmission(@RequestBody Submission submission) {
        submission.setCreatedAt(LocalDateTime.now());

        // Save submission first
        Submission savedSubmission = submissionRepository.save(submission);

        // Create feedback (TEMP AI SIMULATION)
        Feedback feedback = new Feedback();
        feedback.setSubmissionId(savedSubmission.getId());
        feedback.setUserId(UUID.randomUUID()); // temporary
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
