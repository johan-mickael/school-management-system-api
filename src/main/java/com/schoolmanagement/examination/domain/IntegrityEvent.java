package com.schoolmanagement.examination.domain;

import java.time.Instant;

import com.schoolmanagement.examination.domain.exception.InvalidIntegrityEvent;

public record IntegrityEvent(IntegrityEventType type, Instant occurredAt) {
    public IntegrityEvent {
        if (type == null || occurredAt == null) {
            throw new InvalidIntegrityEvent();
        }
    }
}
