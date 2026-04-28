package com.group3.backend.controller;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group3.backend.entity.Feedback;

@SpringBootTest
@AutoConfigureMockMvc
public class FeedbackControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateFeedback() throws Exception {
        UUID userId = UUID.randomUUID();

        Feedback feedback = new Feedback();
        feedback.setUserId(userId);
        feedback.setFeedbackText("Test feedback");
        feedback.setScore(80);

        mockMvc.perform(post("/submissions/1/feedback")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(feedback)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.feedbackText").value("Test feedback"))
                .andExpect(jsonPath("$.score").value(80));
    }

    @Test
    void testGetFeedbackByUser() throws Exception {
        UUID userId = UUID.randomUUID();

        Feedback feedback = new Feedback();
        feedback.setUserId(userId);
        feedback.setFeedbackText("Another test");
        feedback.setScore(70);

        mockMvc.perform(post("/submissions/2/feedback")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(feedback)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/submissions/user/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(userId.toString()));
    }

    @Test
    void testGetFeedbackBySubmission() throws Exception {
        UUID userId = UUID.randomUUID();

        Feedback feedback = new Feedback();
        feedback.setUserId(userId);
        feedback.setFeedbackText("Submission test");
        feedback.setScore(90);

        mockMvc.perform(post("/submissions/3/feedback")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(feedback)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/submissions/3/feedback"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.submissionId").value(3)); 
    }
}