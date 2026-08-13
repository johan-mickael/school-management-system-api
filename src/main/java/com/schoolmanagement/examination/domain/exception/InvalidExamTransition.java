package com.schoolmanagement.examination.domain.exception;

import com.schoolmanagement.examination.domain.ExamId;
import com.schoolmanagement.examination.domain.ExamStatus;
import com.schoolmanagement.shared.domain.DomainException;

public final class InvalidExamTransition extends DomainException {
    public InvalidExamTransition(ExamId id, ExamStatus currentStatus, String action) {
        super("Cannot %s exam '%s' from status '%s'".formatted(action, id, currentStatus));
    }
}
