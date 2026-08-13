package com.schoolmanagement.iam.domain.exception;

import com.schoolmanagement.shared.domain.DomainException;

public final class InvalidUserId extends DomainException {
    public InvalidUserId(String raw) {
        super("Invalid user id: '%s'".formatted(raw));
    }
}
