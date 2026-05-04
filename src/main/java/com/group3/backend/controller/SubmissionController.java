package com.group3.backend.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group3.backend.entity.Feedback;
import com.group3.backend.entity.Problem;
import com.group3.backend.entity.Submission;
import com.group3.backend.repository.FeedbackRepository;
import com.group3.backend.repository.ProblemRepository;
import com.group3.backend.repository.SubmissionRepository;
import com.group3.backend.service.GeminiFeedbackService;

@RestController
@RequestMapping("/api/submissions")
public class SubmissionController {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private ProblemRepository problemRepository;

    @Autowired
    private GeminiFeedbackService geminiFeedbackService;


    @PostMapping
    public Submission createSubmission(@RequestBody Submission submission) {
        if (submission.getProblemId() == null || submission.getUserId() == null) {
            throw new IllegalArgumentException("Problem ID and User ID are required.");
        }

        // submission.setCreatedAt(LocalDateTime.now());
        // Submission savedSubmission = submissionRepository.save(submission);

        // // Generate and save automated feedback
        // Feedback feedback = new Feedback();
        // feedback.setSubmissionId(savedSubmission.getId());
        // // Since Feedback expects a UUID for userId, we check if we can map it
        // // For now, we use a placeholder UUID or the user's ID if applicable
        // feedback.setUserId(UUID.randomUUID()); 
        // feedback.setFeedbackText(generateMockFeedback(savedSubmission.getCode()));
        // feedback.setScore(generateMockScore());
        // feedback.setCreatedAt(LocalDateTime.now());
        // feedbackRepository.save(feedback);

        // return savedSubmission;

        Submission savedSubmission = submissionRepository.save(submission);

        Problem problem = null;
        if (savedSubmission.getProblemId() != null) {
            problem = problemRepository.findById(savedSubmission.getProblemId()).orElse(null);
        }

        GeminiFeedbackService.FeedbackResult aiResult =
        geminiFeedbackService.generateFeedback(savedSubmission, problem);

        Feedback feedback = new Feedback();
        feedback.setSubmissionId(savedSubmission.getId());
        feedback.setUserId(UUID.randomUUID());
        feedback.setFeedbackText(aiResult.feedbackText());
        feedback.setScore(aiResult.score());
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
    public List<Submission> getUserSubmissions(@PathVariable UUID userId) {
        return submissionRepository.findByUserId(userId);
    }

}
