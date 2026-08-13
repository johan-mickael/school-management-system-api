package com.schoolmanagement.attendance.presentation.dto;

import jakarta.validation.constraints.NotBlank;

public record JustifyAttendanceRequest(
    @NotBlank String justification) {
}
