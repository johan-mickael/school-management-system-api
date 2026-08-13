package com.schoolmanagement.examination.domain.exception;

import com.schoolmanagement.shared.domain.DomainException;

public final class InvalidAttemptId extends DomainException {
    public InvalidAttemptId(String raw) {
        super("Invalid attempt id: '%s'".formatted(raw));
    }
}
