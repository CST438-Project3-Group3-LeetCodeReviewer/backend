package com.group3.backend.controller;

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
    private SubmissionRepository submissionRepository;

    @PostMapping
    public Submission createSubmission(@RequestBody Submission submission) {
        submission.setCreatedAt(LocalDateTime.now());
        return submissionRepository.save(submission);
    }

    @GetMapping("/user/{userId}")
    public List<Submission> getUserSubmissions(@PathVariable Long userId) {
        return submissionRepository.findByUserId(userId);
    }
}
