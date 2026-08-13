package com.schoolmanagement.iam.domain;

public interface PasswordHasher {
    PasswordHash hash(String rawPassword);

    boolean matches(String rawPassword, PasswordHash hash);
}
