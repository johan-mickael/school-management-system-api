package com.schoolmanagement.examination.domain.exception;

import com.schoolmanagement.examination.domain.AttemptId;
import com.schoolmanagement.examination.domain.AttemptStatus;
import com.schoolmanagement.shared.domain.DomainException;

public final class InvalidAttemptTransition extends DomainException {
    public InvalidAttemptTransition(AttemptId id, AttemptStatus currentStatus, String action) {
        super("Cannot %s attempt '%s' from status '%s'".formatted(action, id, currentStatus));
    }
}
