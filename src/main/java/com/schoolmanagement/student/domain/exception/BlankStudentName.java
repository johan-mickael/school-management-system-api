package com.schoolmanagement.student.domain.exception;

import com.schoolmanagement.shared.domain.DomainException;

public final class BlankStudentName extends DomainException {
    public BlankStudentName() {
        super("Student first and last name must not be blank");
    }
}