package com.schoolmanagement.examination.domain;

import java.util.UUID;

import com.schoolmanagement.examination.domain.exception.InvalidAttemptId;

public record AttemptId(UUID value) {
    public AttemptId {
        if (null == value) {
            throw new InvalidAttemptId("null");
        }
    }

    public static AttemptId generate() {
        return new AttemptId(UUID.randomUUID());
    }

    public static AttemptId of(String raw) {
        try {
            return new AttemptId(UUID.fromString(raw));
        } catch (IllegalArgumentException e) {
            throw new InvalidAttemptId(raw);
        }
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
