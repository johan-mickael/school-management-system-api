package com.schoolmanagement.iam.domain;

import java.util.UUID;

import com.schoolmanagement.iam.domain.exception.InvalidUserId;

public record UserId(UUID value) {
    public UserId {
        if (null == value) {
            throw new InvalidUserId("null");
        }
    }

    public static UserId generate() {
        return new UserId(UUID.randomUUID());
    }

    public static UserId of(String raw) {
        try {
            return new UserId(UUID.fromString(raw));
        } catch (IllegalArgumentException e) {
            throw new InvalidUserId(raw);
        }
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
