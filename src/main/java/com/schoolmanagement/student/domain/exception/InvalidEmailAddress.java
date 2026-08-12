package com.schoolmanagement.student.domain.exception;

import com.schoolmanagement.shared.domain.DomainException;

public final class InvalidEmailAddress extends DomainException {
    public InvalidEmailAddress(String raw) {
        super("Invalid email address: '%s'".formatted(raw));
    }
}