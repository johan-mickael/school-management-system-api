package com.schoolmanagement.teacher.domain.exception;

import com.schoolmanagement.shared.domain.DomainException;
import com.schoolmanagement.teacher.domain.TeacherId;

public final class TeacherNotFound extends DomainException {
    public TeacherNotFound(TeacherId id) {
        super("Teacher '%s' was not found".formatted(id));
    }
}
