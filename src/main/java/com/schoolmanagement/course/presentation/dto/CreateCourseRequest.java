package com.schoolmanagement.course.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record CreateCourseRequest(
    @NotBlank String code,
    @NotBlank String title,
    @Positive double coefficient,
    @NotBlank String promotionId,
    String teacherId) {
}
