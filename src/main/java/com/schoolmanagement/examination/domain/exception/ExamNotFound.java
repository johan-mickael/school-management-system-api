package com.schoolmanagement.examination.domain.exception;

import com.schoolmanagement.examination.domain.ExamId;
import com.schoolmanagement.shared.domain.DomainException;

public final class ExamNotFound extends DomainException {
    public ExamNotFound(ExamId id) {
        super("Exam '%s' was not found".formatted(id));
    }
}
