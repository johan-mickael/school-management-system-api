package com.schoolmanagement.student.domain.exception;

import com.schoolmanagement.shared.domain.DomainException;
import com.schoolmanagement.student.domain.StudentId;

public final class StudentAlreadyArchived extends DomainException {
    public StudentAlreadyArchived(StudentId id) {
        super("Student '%s' is already archived".formatted(id));
    }
}