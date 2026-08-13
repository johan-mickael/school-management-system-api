package com.schoolmanagement.teacher.domain.exception;

import com.schoolmanagement.shared.domain.DomainException;

public final class InvalidTeacherId extends DomainException {
    public InvalidTeacherId(String raw) {
        super("Invalid teacher id: '%s'".formatted(raw));
    }
}
