package com.schoolmanagement.examination.presentation.dto;

import java.time.Instant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ScheduleExamRequest(
    @NotBlank String courseId,
    @NotBlank String promotionId,
    @NotNull Instant start,
    @NotNull Instant end) {
}
