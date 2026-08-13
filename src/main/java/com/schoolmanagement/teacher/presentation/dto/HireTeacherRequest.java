package com.schoolmanagement.teacher.presentation.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record HireTeacherRequest(
    @NotBlank String staffNumber,
    @NotBlank String firstName,
    @NotBlank String lastName,
    @NotBlank @Email String email) {
}
