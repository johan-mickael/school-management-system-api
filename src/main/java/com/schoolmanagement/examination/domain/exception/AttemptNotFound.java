package com.schoolmanagement.examination.domain.exception;

import com.schoolmanagement.examination.domain.AttemptId;
import com.schoolmanagement.shared.domain.DomainException;

public final class AttemptNotFound extends DomainException {
    public AttemptNotFound(AttemptId id) {
        super("Exam attempt '%s' was not found".formatted(id));
    }
}
