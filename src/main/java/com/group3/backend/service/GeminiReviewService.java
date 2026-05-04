package com.group3.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.group3.backend.entity.Problem;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class GeminiReviewService {
    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final String apiKey;
    private final String model;

    public GeminiReviewService(
            RestClient.Builder restClientBuilder,
            ObjectMapper objectMapper,
            @Value("${gemini.api-key:${GEMINI_API_KEY:${GOOGLE_API_KEY:}}}") String apiKey,
            @Value("${gemini.model:${GEMINI_MODEL:gemini-2.5-flash}}") String model) {
        this.restClient = restClientBuilder.baseUrl("https://generativelanguage.googleapis.com").build();
        this.objectMapper = objectMapper;
        this.apiKey = apiKey;
        this.model = model;
    }

    public ReviewResult reviewSubmission(Problem problem, String code, String language) {
        if (apiKey == null || apiKey.isBlank()) {
            return fallbackReview(code, "Gemini API key is not configured.");
        }

        try {
            Map<String, Object> requestBody = Map.of(
                    "contents", List.of(
                            Map.of("parts", List.of(Map.of("text", buildPrompt(problem, code, language))))
                    ),
                    "generationConfig", Map.of(
                            "temperature", 0.2,
                            "responseMimeType", "application/json"
                    )
            );

            JsonNode response = restClient.post()
                    .uri("/v1beta/models/{model}:generateContent?key={apiKey}", model, apiKey)
                    .body(requestBody)
                    .retrieve()
                    .body(JsonNode.class);

            String rawText = response
                    .path("candidates")
                    .path(0)
                    .path("content")
                    .path("parts")
                    .path(0)
                    .path("text")
                    .asText();

            return parseReview(rawText);
        } catch (Exception error) {
            return fallbackReview(code, "Gemini review failed: " + error.getMessage());
        }
    }

    private String buildPrompt(Problem problem, String code, String language) {
        String title = problem == null ? "Unknown problem" : problem.getTitle();
        String description = problem == null ? "No problem description provided." : problem.getDescription();

        ProblemCheckSpec checkSpec = getProblemCheckSpec(problem);
        String requiredSignature = getRequiredSignature(problem);

        return """
                You are a coding interview coach reviewing a LeetCode-style submission.
                Decide whether the submitted code should pass. Be strict about the required
                function name, parameter count, parameter order, return type, sample tests, and
                common edge cases. Reject starter-code placeholders, code that only prints, code
                with syntax errors, or code that ignores required parameters.

                Return only valid JSON in this exact shape:
                {
                  "passed": true,
                  "status": "Accepted",
                  "score": 85,
                  "feedback": "Short user-facing feedback with specific improvements."
                }

                Problem title: %s
                Problem description: %s
                Required function signature: %s
                Starter code:
                ```%s
                %s
                ```
                Sample and edge cases to consider:
                %s
                Acceptance criteria:
                %s
                Language: %s
                Submission:
                ```%s
                %s
                ```
                """.formatted(
                title,
                description,
                requiredSignature,
                normalizeLanguage(language),
                problem == null ? "No starter code provided." : problem.getStarterCode(),
                checkSpec.testCases(),
                checkSpec.acceptanceCriteria(),
                normalizeLanguage(language),
                normalizeLanguage(language),
                code
        );
    }

    private ReviewResult parseReview(String rawText) throws Exception {
        JsonNode review = objectMapper.readTree(extractJson(rawText));
        boolean passed = review.path("passed").asBoolean(false);
        int score = Math.max(0, Math.min(100, review.path("score").asInt(passed ? 80 : 50)));
        String feedback = review.path("feedback").asText("No detailed feedback returned.");
        String status = passed ? "Accepted" : "Wrong Answer";
        if (review.hasNonNull("status")) {
            String candidateStatus = review.path("status").asText();
            status = "Accepted".equalsIgnoreCase(candidateStatus) ? "Accepted" : "Wrong Answer";
            passed = "Accepted".equals(status);
        }

        String feedbackText = "Result: " + status + "\n\n" + feedback;
        return new ReviewResult(passed, status, score, feedbackText);
    }

    private ReviewResult fallbackReview(String code, String reason) {
        int score = code != null && code.contains("def ") ? 40 : 20;
        String status = "Wrong Answer";
        String feedback = "Result: Wrong Answer\n\nThe submission could not be verified against the problem requirements. Check that your Gemini API key is configured, then submit again for a real pass/fail review.";

        return new ReviewResult(false, status, score, feedback + "\n\nNote: " + reason);
    }

    private String extractJson(String rawText) {
        if (rawText == null) {
            return "{}";
        }

        String trimmed = rawText.trim();
        int start = trimmed.indexOf('{');
        int end = trimmed.lastIndexOf('}');
        if (start >= 0 && end >= start) {
            return trimmed.substring(start, end + 1);
        }

        return trimmed;
    }

    private String getRequiredSignature(Problem problem) {
        if (problem == null || problem.getStarterCode() == null) {
            return "Use the function signature implied by the problem.";
        }

        return problem.getStarterCode().lines()
                .map(String::trim)
                .filter(line -> line.startsWith("def "))
                .findFirst()
                .orElse("Use the starter code function signature.");
    }

    private ProblemCheckSpec getProblemCheckSpec(Problem problem) {
        String title = problem == null || problem.getTitle() == null ? "" : problem.getTitle().toLowerCase();

        if (title.contains("two sum")) {
            return new ProblemCheckSpec(
                    """
                            two_sum([2, 7, 11, 15], 9) should return [0, 1] or [1, 0]
                            two_sum([3, 2, 4], 6) should return [1, 2] or [2, 1]
                            two_sum([3, 3], 6) should return [0, 1] or [1, 0]
                            """,
                    "Return the two distinct indices whose values add to target. Do not return values, reuse one index, or hard-code the sample."
            );
        }

        if (title.contains("valid parentheses")) {
            return new ProblemCheckSpec(
                    """
                            is_valid("()[]{}") should return true
                            is_valid("(]") should return false
                            is_valid("([)]") should return false
                            is_valid("{[]}") should return true
                            """,
                    "Use a stack-like approach or equivalent logic so nested and ordered brackets are validated correctly."
            );
        }

        if (title.contains("binary search")) {
            return new ProblemCheckSpec(
                    """
                            search([-1, 0, 3, 5, 9, 12], 9) should return 4
                            search([-1, 0, 3, 5, 9, 12], 2) should return -1
                            search([5], 5) should return 0
                            search([5], -5) should return -1
                            """,
                    "Return the target index when present and -1 otherwise. The solution should work for empty, single-item, and normal sorted arrays."
            );
        }

        return new ProblemCheckSpec(
                "Use the examples and edge cases implied by the problem statement.",
                "The code must implement the required function, use all required parameters correctly, and return the expected value."
        );
    }

    private String normalizeLanguage(String language) {
        if (language == null || language.isBlank()) {
            return "python";
        }

        return language.trim().toLowerCase();
    }

    private record ProblemCheckSpec(String testCases, String acceptanceCriteria) {
    }

    public record ReviewResult(boolean passed, String status, int score, String feedbackText) {
    }
}
