package com.schoolmanagement.course.domain.exception;

import com.schoolmanagement.shared.domain.DomainException;

public final class BlankCourseTitle extends DomainException {
    public BlankCourseTitle() {
        super("Course title must not be blank");
    }
}
