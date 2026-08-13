package com.schoolmanagement.examination.domain.exception;

import com.schoolmanagement.shared.domain.DomainException;

public final class InvalidExamId extends DomainException {
    public InvalidExamId(String raw) {
        super("Invalid exam id: '%s'".formatted(raw));
    }
}
