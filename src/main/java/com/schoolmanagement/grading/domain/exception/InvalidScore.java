package com.schoolmanagement.grading.domain.exception;

import com.schoolmanagement.shared.domain.DomainException;

public final class InvalidScore extends DomainException {
    public InvalidScore(double raw) {
        super("Invalid score: '%s'. Score must be between 0 and 20 inclusive".formatted(raw));
    }
}
