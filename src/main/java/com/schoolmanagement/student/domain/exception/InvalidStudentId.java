package com.schoolmanagement.student.domain.exception;

import com.schoolmanagement.shared.domain.DomainException;

public final class InvalidStudentId extends DomainException {
    public InvalidStudentId(String raw) {
        super("Invalid student id: '%s'".formatted(raw));
    }
}