package com.schoolmanagement.iam.domain.exception;

import com.schoolmanagement.shared.domain.DomainException;

public final class InvalidRole extends DomainException {
    public InvalidRole(String raw) {
        super("Invalid role: '%s'".formatted(raw));
    }
}
