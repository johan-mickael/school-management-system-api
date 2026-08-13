package com.schoolmanagement.examination.domain.exception;

import com.schoolmanagement.examination.domain.ExamId;
import com.schoolmanagement.shared.domain.DomainException;

public final class ExamNotOpenForAttempt extends DomainException {
    public ExamNotOpenForAttempt(ExamId examId) {
        super("Exam '%s' is not open; cannot start an attempt".formatted(examId));
    }
}
