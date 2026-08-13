package com.schoolmanagement.teacher.domain;

import java.util.UUID;

import com.schoolmanagement.teacher.domain.exception.InvalidTeacherId;

public record TeacherId(UUID value) {
    public TeacherId {
        if (null == value) {
            throw new InvalidTeacherId("null");
        }
    }

    public static TeacherId generate() {
        return new TeacherId(UUID.randomUUID());
    }

    public static TeacherId of(String raw) {
        try {
            return new TeacherId(UUID.fromString(raw));
        } catch (IllegalArgumentException e) {
            throw new InvalidTeacherId(raw);
        }
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
