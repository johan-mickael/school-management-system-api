package com.schoolmanagement.promotion.presentation.dto;

import jakarta.validation.constraints.NotBlank;

public record AssignStudentToPromotionRequest(
    @NotBlank String studentId) {
}
