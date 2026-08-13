package com.schoolmanagement.shared.domain.exception;

import java.time.Instant;

import com.schoolmanagement.shared.domain.DomainException;

public final class InvalidTimeWindow extends DomainException {
    public InvalidTimeWindow(Instant start, Instant end) {
        super("Invalid time window: start='%s' end='%s'. End must be after start".formatted(start, end));
    }
}
