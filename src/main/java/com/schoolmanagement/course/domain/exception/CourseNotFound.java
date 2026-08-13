package com.schoolmanagement.course.domain.exception;

import com.schoolmanagement.shared.domain.DomainException;
import com.schoolmanagement.course.domain.CourseId;

public final class CourseNotFound extends DomainException {
    public CourseNotFound(CourseId id) {
        super("Course '%s' was not found".formatted(id));
    }
}
