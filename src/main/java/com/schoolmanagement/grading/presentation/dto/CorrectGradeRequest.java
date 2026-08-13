package com.schoolmanagement.grading.presentation.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;

public record CorrectGradeRequest(
    @DecimalMin("0.0") @DecimalMax("20.0") double score) {
}
