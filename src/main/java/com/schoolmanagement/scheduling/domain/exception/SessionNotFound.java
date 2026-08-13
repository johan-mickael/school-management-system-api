package com.schoolmanagement.scheduling.domain.exception;

import com.schoolmanagement.scheduling.domain.SessionId;
import com.schoolmanagement.shared.domain.DomainException;

public final class SessionNotFound extends DomainException {
    public SessionNotFound(SessionId id) {
        super("Session '%s' was not found".formatted(id));
    }
}
