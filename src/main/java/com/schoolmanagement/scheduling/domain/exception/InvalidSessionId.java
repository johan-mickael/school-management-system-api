package com.schoolmanagement.scheduling.domain.exception;

import com.schoolmanagement.shared.domain.DomainException;

public final class InvalidSessionId extends DomainException {
    public InvalidSessionId(String raw) {
        super("Invalid session id: '%s'".formatted(raw));
    }
}
