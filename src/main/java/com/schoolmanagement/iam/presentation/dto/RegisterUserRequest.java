package com.schoolmanagement.iam.presentation.dto;

import jakarta.validation.constraints.NotBlank;

public record RegisterUserRequest(
    @NotBlank String username,
    @NotBlank String password,
    @NotBlank String role,
    String personId) {
}
