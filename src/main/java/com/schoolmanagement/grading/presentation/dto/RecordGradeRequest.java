package com.schoolmanagement.grading.presentation.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record RecordGradeRequest(
    @NotBlank String studentId,
    String examId,
    @DecimalMin("0.0") @DecimalMax("20.0") double score,
    @Positive double coefficient) {
}
