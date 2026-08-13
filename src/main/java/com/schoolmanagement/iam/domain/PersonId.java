package com.schoolmanagement.iam.domain;

import java.util.UUID;

public record PersonId(UUID value) {
    @Override
    public String toString() {
        return value.toString();
    }
}
