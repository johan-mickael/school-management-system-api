package com.schoolmanagement.iam.domain.exception;

import com.schoolmanagement.shared.domain.DomainException;

public final class InvalidCredentials extends DomainException {
    public InvalidCredentials() {
        super("Invalid username or password");
    }
}
