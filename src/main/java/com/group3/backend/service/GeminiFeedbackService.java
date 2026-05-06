package com.group3.backend.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.group3.backend.entity.Problem;
import com.group3.backend.entity.Submission;

@Service
public class GeminiFeedbackService {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    @Value("${gemini.api.key:}")
    private String apiKey;

    @Value("${gemini.model:gemini-2.5-flash}")
    private String model;

    public GeminiFeedbackService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.restClient = RestClient.builder()
                .baseUrl("https://generativelanguage.googleapis.com/v1beta")
                .build();
    }

    public FeedbackResult generateFeedback(Submission submission, Problem problem) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("GEMINI_API_KEY is not set");
        }

        String prompt = """
                You are a coding coach reviewing a student's code.

                Give feedback on:
                - correctness
                - edge cases
                - time complexity
                - space complexity
                - readability
                - weaknesses or likely bugs

                Problem title: %s
                Difficulty: %s

                Problem description:
                %s

                Starter code:
                %s

                Student submitted code:
                %s

                Be helpful, specific, and honest.
                """
                .formatted(
                        problem != null ? problem.getTitle() : "Unknown problem",
                        problem != null ? problem.getDifficulty() : "Unknown difficulty",
                        problem != null ? problem.getDescription() : "No description available",
                        problem != null ? problem.getStarterCode() : "No starter code available",
                        submission.getCode()
                );

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of(
                                "parts", List.of(
                                        Map.of("text", prompt)
                                )
                        )
                ),
                "generationConfig", Map.of(
                        "responseMimeType", "application/json",
                        "responseJsonSchema", Map.of(
                                "type", "object",
                                "properties", Map.of(
                                        "score", Map.of(
                                                "type", "integer",
                                                "description", "A score from 0 to 100"
                                        ),
                                        "feedbackText", Map.of(
                                                "type", "string",
                                                "description", "Detailed feedback for the student"
                                        )
                                ),
                                "required", List.of("score", "feedbackText")
                        )
                )
        );

        JsonNode response = restClient.post()
                .uri("/models/{model}:generateContent", model)
                .header("x-goog-api-key", apiKey)
                .header("Content-Type", "application/json")
                .body(requestBody)
                .retrieve()
                .body(JsonNode.class);

        try {
            String text = response
                    .path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();

            JsonNode feedbackJson = objectMapper.readTree(text);

            return new FeedbackResult(
                    feedbackJson.path("feedbackText").asText(),
                    feedbackJson.path("score").asInt()
            );
        } catch (Exception e) {
            throw new RuntimeException("Could not parse Gemini feedback response", e);
        }
    }

    public record FeedbackResult(String feedbackText, int score) {}
}
