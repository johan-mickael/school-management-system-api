package com.schoolmanagement.iam.domain;

import com.schoolmanagement.iam.domain.exception.InvalidRole;

public enum Role {
    ADMIN,
    TEACHER,
    STUDENT;

    /**
     * @throws InvalidRole if raw is not a known role name
     */
    public static Role of(String raw) {
        try {
            return Role.valueOf(raw == null ? "" : raw.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidRole(raw);
        }
    }
}
