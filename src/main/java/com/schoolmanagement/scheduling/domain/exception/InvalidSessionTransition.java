package com.schoolmanagement.scheduling.domain.exception;

import com.schoolmanagement.scheduling.domain.SessionId;
import com.schoolmanagement.scheduling.domain.SessionStatus;
import com.schoolmanagement.shared.domain.DomainException;

public final class InvalidSessionTransition extends DomainException {
    public InvalidSessionTransition(SessionId id, SessionStatus currentStatus, String action) {
        super("Cannot %s session '%s' from status '%s'".formatted(action, id, currentStatus));
    }
}
