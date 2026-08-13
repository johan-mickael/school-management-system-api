package com.schoolmanagement.scheduling.domain;

import java.util.UUID;

import com.schoolmanagement.scheduling.domain.exception.InvalidSessionId;

public record SessionId(UUID value) {
    public SessionId {
        if (null == value) {
            throw new InvalidSessionId("null");
        }
    }

    public static SessionId generate() {
        return new SessionId(UUID.randomUUID());
    }

    public static SessionId of(String raw) {
        try {
            return new SessionId(UUID.fromString(raw));
        } catch (IllegalArgumentException e) {
            throw new InvalidSessionId(raw);
        }
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
