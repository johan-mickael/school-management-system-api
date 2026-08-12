package com.schoolmanagement.student.presentation.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EnrollStudentRequest(
    @NotBlank String studentNumber,
    @NotBlank String firstName,
    @NotBlank String lastName,
    @NotBlank @Email String email) {
}
