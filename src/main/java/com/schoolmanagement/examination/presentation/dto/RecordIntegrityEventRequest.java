package com.schoolmanagement.examination.presentation.dto;

import jakarta.validation.constraints.NotBlank;

public record RecordIntegrityEventRequest(
    @NotBlank String eventType) {
}
