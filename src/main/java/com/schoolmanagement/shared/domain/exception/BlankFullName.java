package com.schoolmanagement.shared.domain.exception;

import com.schoolmanagement.shared.domain.DomainException;

public final class BlankFullName extends DomainException {
    public BlankFullName() {
        super("Full name must not be blank");
    }
}
