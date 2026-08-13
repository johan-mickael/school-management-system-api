package com.schoolmanagement.iam.domain;

import com.schoolmanagement.iam.domain.exception.BlankPasswordHash;

public record PasswordHash(String value) {
    public PasswordHash {
        if (value == null || value.isBlank()) {
            throw new BlankPasswordHash();
        }
    }
}
