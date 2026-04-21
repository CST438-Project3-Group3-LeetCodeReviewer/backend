package com.group3.backend.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Feedback {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    private long submissionId;
    private Long userId;

    @Column(columnDefinition= "TEXT")
    private String feedbackText;

    private Integer score;
    private LocalDateTime createdAt;

    public Feedback(){

    }

    public Feedback(Long submissionId,Long userId,String feedbackText, Integer score,LocalDateTime createdAt){
        this.submissionId = submissionId;
        this.userId = userId;
        this.feedbackText = feedbackText;
        this.score = score;
        this.createdAt = createdAt;
    }

    public Long getId(){
        return id;
    }
    public Long getSubmissionId(){
        return submissionId;
    }
    
    public void setSubmissionId(Long submissionId){
        this.submissionId = submissionId;
    }

    public Long getUserId(){
        return userId;
    }
    public void setUserId(Long userId){
        this.userId = userId;
    }
    public String getFeedbackText(){
        return feedbackText;
    }

    public void setFeedbackText(String feedbackText){
        this.feedbackText = feedbackText;
    }
    public Integer getScore(){
        return score;
    }
    public void setScore(Integer score){
        this.score = score;
    }
    public LocalDateTime getCreatedAt(){
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt){
        this.createdAt = createdAt;
    }

}
