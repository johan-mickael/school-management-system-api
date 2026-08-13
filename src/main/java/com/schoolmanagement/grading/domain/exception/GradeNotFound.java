package com.schoolmanagement.grading.domain.exception;

import com.schoolmanagement.grading.domain.GradeId;
import com.schoolmanagement.shared.domain.DomainException;

public final class GradeNotFound extends DomainException {
    public GradeNotFound(GradeId id) {
        super("Grade '%s' was not found".formatted(id));
    }
}
