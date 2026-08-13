package com.schoolmanagement.scheduling.presentation.dto;

import java.time.Instant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record ScheduleSessionRequest(
    @NotBlank String courseId,
    @NotBlank String promotionId,
    @NotBlank String teacherId,
    @NotNull Instant start,
    @NotNull Instant end,
    @PositiveOrZero long gracePeriodSeconds) {
}
