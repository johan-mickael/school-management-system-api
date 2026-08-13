package com.schoolmanagement.grading.domain.exception;

import com.schoolmanagement.shared.domain.DomainException;

public final class InvalidGradeId extends DomainException {
    public InvalidGradeId(String raw) {
        super("Invalid grade id: '%s'".formatted(raw));
    }
}
