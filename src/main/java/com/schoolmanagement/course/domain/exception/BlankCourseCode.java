package com.schoolmanagement.course.domain.exception;

import com.schoolmanagement.shared.domain.DomainException;

public final class BlankCourseCode extends DomainException {
    public BlankCourseCode() {
        super("Course code must not be blank");
    }
}
