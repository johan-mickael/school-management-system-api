package com.schoolmanagement.student.domain;

import java.util.UUID;

import com.schoolmanagement.student.domain.exception.InvalidStudentId;

public record StudentId(UUID value) {
    public StudentId {
        if (null == value) {
            throw new InvalidStudentId("null");
        }
    }

    public static StudentId generate() {
        return new StudentId(UUID.randomUUID());
    }

    public static StudentId of(String raw) {
        try {
            return new StudentId(UUID.fromString(raw));
        } catch (IllegalArgumentException e) {
            throw new InvalidStudentId(raw);
        }
    }

    @Override
    public String toString() {
        return value.toString();
    }
}