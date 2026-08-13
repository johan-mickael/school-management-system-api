package com.schoolmanagement.student.domain.exception;

import com.schoolmanagement.shared.domain.DomainException;
import com.schoolmanagement.student.domain.StudentId;

public final class StudentAlreadyAssignedToPromotion extends DomainException {
    public StudentAlreadyAssignedToPromotion(StudentId id) {
        super("Student '%s' is already assigned to a promotion".formatted(id));
    }
}
