package com.group3.backend.controller;

import com.group3.backend.dto.SubmissionRequest;
import com.group3.backend.entity.Problem;
import com.group3.backend.repository.FeedbackRepository;

import com.group3.backend.entity.Feedback;
import com.group3.backend.entity.Submission;
import com.group3.backend.repository.ProblemRepository;
import com.group3.backend.repository.SubmissionRepository;
import com.group3.backend.service.GeminiReviewService;
import com.group3.backend.service.GeminiReviewService.ReviewResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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
    private GeminiReviewService geminiReviewService;

    @PostMapping
    public Submission createSubmission(@RequestBody SubmissionRequest request) {
        if (request.getProblemId() == null) {
            throw new IllegalArgumentException("Problem ID is required.");
        }

        String language = normalizeLanguage(request.getLanguage());
        UUID userId = parseUserId(request.getUserId());
        Submission submission = new Submission();
        submission.setProblemId(request.getProblemId());
        submission.setUserId(userId);
        submission.setCode(request.getCode());
        submission.setTimeTaken(request.getTimeTaken());
        submission.setStatus(request.getStatus() == null ? "Submitted" : request.getStatus());
        submission.setCreatedAt(LocalDateTime.now());

        Submission savedSubmission = submissionRepository.save(submission);

        Problem problem = problemRepository.findById(savedSubmission.getProblemId()).orElse(null);
        ReviewResult review = geminiReviewService.reviewSubmission(problem, savedSubmission.getCode(), language);
        savedSubmission.setStatus(review.status());
        savedSubmission = submissionRepository.save(savedSubmission);

        Feedback feedback = new Feedback();
        feedback.setSubmissionId(savedSubmission.getId());
        feedback.setUserId(userId);
        feedback.setFeedbackText(review.feedbackText());
        feedback.setScore(review.score());
        feedback.setCreatedAt(LocalDateTime.now());

        feedbackRepository.save(feedback);

        return savedSubmission;
    }

    private String normalizeLanguage(String language) {
        if (language == null || language.isBlank()) {
            return "python";
        }

        return language.trim().toLowerCase();
    }

    @GetMapping("/user/{userId}")
    public List<Submission> getUserSubmissions(@PathVariable UUID userId) {
        return submissionRepository.findByUserId(userId);
    }

    private UUID parseUserId(String userId) {
        if (userId == null || userId.isBlank()) {
            return null;
        }

        try {
            return UUID.fromString(userId);
        } catch (IllegalArgumentException error) {
            throw new IllegalArgumentException("User ID must be a valid UUID.");
        }
    }
}
