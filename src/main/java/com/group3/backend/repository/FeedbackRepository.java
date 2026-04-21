package com.group3.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.group3.backend.entity.Feedback;

public interface FeedbackRepository extends JpaRepository<Feedback,Long> {
    List<Feedback> findbyUserId(Long userId);
    List<Feedback> findbySubmissionId(Long submissionId);
}
