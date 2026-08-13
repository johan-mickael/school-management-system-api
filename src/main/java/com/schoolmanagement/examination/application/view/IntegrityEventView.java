package com.schoolmanagement.examination.application.view;

import java.time.Instant;

import com.schoolmanagement.examination.domain.IntegrityEvent;

public record IntegrityEventView(String type, Instant occurredAt) {

    public static IntegrityEventView from(IntegrityEvent e) {
        return new IntegrityEventView(e.type().name(), e.occurredAt());
    }
}
