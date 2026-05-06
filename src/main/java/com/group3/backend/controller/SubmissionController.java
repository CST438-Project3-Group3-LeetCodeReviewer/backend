package com.group3.backend.controller;

import com.group3.backend.dto.SubmissionRequest;
import com.group3.backend.dto.SubmissionUpdateRequest;
import com.group3.backend.entity.Problem;
import com.group3.backend.repository.FeedbackRepository;

import com.group3.backend.entity.Feedback;
import com.group3.backend.entity.Submission;
import com.group3.backend.repository.ProblemRepository;
import com.group3.backend.repository.SubmissionRepository;
import com.group3.backend.service.GeminiReviewService;
import com.group3.backend.service.GeminiReviewService.ReviewResult;
import com.group3.backend.service.SupabaseTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/submissions")
@CrossOrigin
public class SubmissionController {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private ProblemRepository problemRepository;

    @Autowired
    private GeminiReviewService geminiReviewService;

    @Autowired
    private SupabaseTokenService tokenService;

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

    @GetMapping("/{id}")
    public ResponseEntity<Submission> getSubmission(
            @PathVariable Long id,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Optional<UUID> userId = resolveUserIdFromAuth(authHeader);
        if (userId.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return submissionRepository.findById(id)
                .filter(sub -> userId.get().equals(sub.getUserId()))
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Submission> updateSubmission(
            @PathVariable Long id,
            @RequestBody SubmissionUpdateRequest body,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Optional<UUID> userId = resolveUserIdFromAuth(authHeader);
        if (userId.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (body.getCode() == null || body.getCode().trim().length() < 10) {
            return ResponseEntity.badRequest().build();
        }

        Optional<Submission> opt = submissionRepository.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Submission submission = opt.get();
        if (!userId.get().equals(submission.getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        String language = normalizeLanguage(body.getLanguage());
        submission.setCode(body.getCode());
        if (body.getTimeTaken() != null) {
            submission.setTimeTaken(body.getTimeTaken());
        }

        Problem problem = problemRepository.findById(submission.getProblemId()).orElse(null);
        ReviewResult review = geminiReviewService.reviewSubmission(problem, submission.getCode(), language);
        submission.setStatus(review.status());

        Submission saved = submissionRepository.save(submission);

        Feedback feedback = feedbackRepository.findBySubmissionId(saved.getId())
                .orElseGet(() -> {
                    Feedback fb = new Feedback();
                    fb.setSubmissionId(saved.getId());
                    fb.setUserId(userId.get());
                    return fb;
                });
        feedback.setUserId(userId.get());
        feedback.setFeedbackText(review.feedbackText());
        feedback.setScore(review.score());
        feedback.setCreatedAt(LocalDateTime.now());
        feedbackRepository.save(feedback);

        return ResponseEntity.ok(saved);
    }

    private Optional<UUID> resolveUserIdFromAuth(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Optional.empty();
        }
        String token = authHeader.substring("Bearer ".length());
        Map<String, Object> claims = tokenService.validateToken(token);
        if (claims == null) {
            return Optional.empty();
        }
        Object sub = claims.get("sub");
        if (sub == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(UUID.fromString(sub.toString()));
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }
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
