package com.schoolmanagement.student.domain.exception;

import com.schoolmanagement.shared.domain.DomainException;
import com.schoolmanagement.student.domain.StudentId;

public final class StudentNotFound extends DomainException {
    public StudentNotFound(StudentId id) {
        super("Student '%s' was not found".formatted(id));
    }
}