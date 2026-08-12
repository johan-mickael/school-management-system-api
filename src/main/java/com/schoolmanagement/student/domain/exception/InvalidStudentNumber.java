package com.schoolmanagement.student.domain.exception;

import com.schoolmanagement.shared.domain.DomainException;

public final class InvalidStudentNumber extends DomainException {
    public InvalidStudentNumber(String raw) {
        super("Invalid student number: '%s' (expected format STU-YYYY-NNNN)".formatted(raw));
    }
}