package com.schoolmanagement.iam.domain.exception;

import com.schoolmanagement.shared.domain.DomainException;

public final class BlankPasswordHash extends DomainException {
    public BlankPasswordHash() {
        super("Password hash must not be blank");
    }
}
