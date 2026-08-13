package com.schoolmanagement.iam.domain;

import com.schoolmanagement.iam.domain.exception.BlankUsername;

public record Username(String value) {
    public Username {
        value = (value == null) ? "" : value.trim().toLowerCase();
        if (value.isEmpty()) {
            throw new BlankUsername();
        }
    }
}
