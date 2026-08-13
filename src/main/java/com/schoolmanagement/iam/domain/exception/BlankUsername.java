package com.schoolmanagement.iam.domain.exception;

import com.schoolmanagement.shared.domain.DomainException;

public final class BlankUsername extends DomainException {
    public BlankUsername() {
        super("Username must not be blank");
    }
}
