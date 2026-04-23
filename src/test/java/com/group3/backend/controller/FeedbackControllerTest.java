package com.group3.backend.controller;
import com.group3.backend.entity.Feedback;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class FeedbackControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateFeedback() throws Exception {
        Feedback feedback = new Feedback();
        feedback.setUserId(1L);
        feedback.setFeedbackText("Test feedback");
        feedback.setScore(80);

        mockMvc.perform(post("/submissions/1/feedback")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(feedback)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.feedbackText").value("Test feedback"))
                .andExpect(jsonPath("$.score").value(80));
    }

    @Test
    void testGetFeedbackByUser() throws Exception {
        // First create feedback
        Feedback feedback = new Feedback();
        feedback.setUserId(2L);
        feedback.setFeedbackText("Another test");
        feedback.setScore(70);

        mockMvc.perform(post("/submissions/2/feedback")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(feedback)))
                .andExpect(status().isOk());

        // Then fetch it
        mockMvc.perform(get("/submissions/feedback/user/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(2));
    }

    @Test
    void testGetFeedbackBySubmission() throws Exception {
        Feedback feedback = new Feedback();
        feedback.setUserId(3L);
        feedback.setFeedbackText("Submission test");
        feedback.setScore(90);

        mockMvc.perform(post("/submissions/3/feedback")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(feedback)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/submissions/3/feedback"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].submissionId").value(3));
    }
}