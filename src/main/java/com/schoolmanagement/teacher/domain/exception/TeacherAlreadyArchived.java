package com.schoolmanagement.teacher.domain.exception;

import com.schoolmanagement.shared.domain.DomainException;
import com.schoolmanagement.teacher.domain.TeacherId;

public final class TeacherAlreadyArchived extends DomainException {
    public TeacherAlreadyArchived(TeacherId id) {
        super("Teacher '%s' is already archived".formatted(id));
    }
}
