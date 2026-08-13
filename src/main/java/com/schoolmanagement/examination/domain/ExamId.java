package com.schoolmanagement.examination.domain;

import java.util.UUID;

import com.schoolmanagement.examination.domain.exception.InvalidExamId;

public record ExamId(UUID value) {
    public ExamId {
        if (null == value) {
            throw new InvalidExamId("null");
        }
    }

    public static ExamId generate() {
        return new ExamId(UUID.randomUUID());
    }

    public static ExamId of(String raw) {
        try {
            return new ExamId(UUID.fromString(raw));
        } catch (IllegalArgumentException e) {
            throw new InvalidExamId(raw);
        }
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
