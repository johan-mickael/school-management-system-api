package com.schoolmanagement.scheduling.domain.exception;

import java.time.Duration;

import com.schoolmanagement.shared.domain.DomainException;

public final class InvalidGracePeriod extends DomainException {
    public InvalidGracePeriod(Duration raw) {
        super("Invalid grace period: '%s'. Must not be negative".formatted(raw));
    }
}
