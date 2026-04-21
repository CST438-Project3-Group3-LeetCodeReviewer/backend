package com.group3.backend.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group3.backend.entity.Feedback;
import com.group3.backend.repository.FeedbackRepository;

@RestController
@RequestMapping("/submissions")
public class FeedbackController {
    @Autowired
    private FeedbackRepository feedbackRepository;

    @PostMapping("/{id}/feedback")
    public Feedback creatFeedback(@PathVariable Long id,@RequestBody Feedback feedback){
        feedback.setSubmissionId(id);
        feedback.setCreatedAt(LocalDateTime.now());
        return feedbackRepository.save(feedback);
    }
    @GetMapping("/feedback/user/{userId}")
    public List<Feedback> getUserFeedback(@PathVariable Long userId){
        return feedbackRepository.findbyUserId(userId);
    }
    @GetMapping("/{id}/feedback")
    public List<Feedback> getSubmissionFeedback(@PathVariable Long id) {
        return feedbackRepository.findbySubmissionId(id);
    }
}
