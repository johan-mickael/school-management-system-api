package com.schoolmanagement.course.domain;

import java.util.UUID;

import com.schoolmanagement.course.domain.exception.InvalidCourseId;

public record CourseId(UUID value) {
    public CourseId {
        if (null == value) {
            throw new InvalidCourseId("null");
        }
    }

    public static CourseId generate() {
        return new CourseId(UUID.randomUUID());
    }

    public static CourseId of(String raw) {
        try {
            return new CourseId(UUID.fromString(raw));
        } catch (IllegalArgumentException e) {
            throw new InvalidCourseId(raw);
        }
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
