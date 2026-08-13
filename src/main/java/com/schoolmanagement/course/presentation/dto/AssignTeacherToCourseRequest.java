package com.schoolmanagement.course.presentation.dto;

import jakarta.validation.constraints.NotBlank;

public record AssignTeacherToCourseRequest(
    @NotBlank String teacherId) {
}
