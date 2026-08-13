package com.schoolmanagement.course.domain.exception;

import com.schoolmanagement.shared.domain.DomainException;

public final class InvalidCourseId extends DomainException {
    public InvalidCourseId(String raw) {
        super("Invalid course id: '%s'".formatted(raw));
    }
}
