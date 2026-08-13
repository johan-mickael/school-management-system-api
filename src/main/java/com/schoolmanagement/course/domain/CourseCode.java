package com.schoolmanagement.course.domain;

import com.schoolmanagement.course.domain.exception.BlankCourseCode;

public record CourseCode(String value) {
    public CourseCode {
        value = (value == null) ? "" : value.trim().toUpperCase();
        if (value.isEmpty()) {
            throw new BlankCourseCode();
        }
    }
}
