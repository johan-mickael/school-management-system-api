package com.schoolmanagement.promotion.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record CreatePromotionRequest(
    @NotBlank String name,
    @NotBlank String academicYear,
    @Positive int capacity) {
}
