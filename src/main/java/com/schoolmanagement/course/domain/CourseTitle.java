package com.schoolmanagement.course.domain;

import com.schoolmanagement.course.domain.exception.BlankCourseTitle;

public record CourseTitle(String value) {
    public CourseTitle {
        value = (value == null) ? "" : value.trim();
        if (value.isEmpty()) {
            throw new BlankCourseTitle();
        }
    }
}
