package com.schoolmanagement.attendance.domain.exception;

import com.schoolmanagement.scheduling.domain.SessionId;
import com.schoolmanagement.shared.domain.DomainException;

public final class SessionSigningNotOpen extends DomainException {
    public SessionSigningNotOpen(SessionId sessionId) {
        super("Session '%s' is not open for signing".formatted(sessionId));
    }
}
