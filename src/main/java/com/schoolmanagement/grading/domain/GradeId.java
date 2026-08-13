package com.schoolmanagement.grading.domain;

import java.util.UUID;

import com.schoolmanagement.grading.domain.exception.InvalidGradeId;

public record GradeId(UUID value) {
    public GradeId {
        if (null == value) {
            throw new InvalidGradeId("null");
        }
    }

    public static GradeId generate() {
        return new GradeId(UUID.randomUUID());
    }

    public static GradeId of(String raw) {
        try {
            return new GradeId(UUID.fromString(raw));
        } catch (IllegalArgumentException e) {
            throw new InvalidGradeId(raw);
        }
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
