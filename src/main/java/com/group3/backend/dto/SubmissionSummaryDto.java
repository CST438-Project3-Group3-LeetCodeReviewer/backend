package com.group3.backend.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record SubmissionSummaryDto(
        Long id,
        Long problemId,
        String problemTitle,
        UUID userId,
        String status,
        Integer timeTaken,
        LocalDateTime createdAt,
        Integer feedbackScore,
        String feedbackPreview
) {
}
