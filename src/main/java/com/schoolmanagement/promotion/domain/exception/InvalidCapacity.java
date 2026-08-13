package com.schoolmanagement.promotion.domain.exception;

import com.schoolmanagement.shared.domain.DomainException;

public final class InvalidCapacity extends DomainException {
    public InvalidCapacity(int raw) {
        super("Invalid capacity: '%d'. Capacity must be greater than zero".formatted(raw));
    }
}
