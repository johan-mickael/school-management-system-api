package com.schoolmanagement.iam.domain.exception;

import com.schoolmanagement.shared.domain.DomainException;

public final class InvalidPersonId extends DomainException {
    public InvalidPersonId(String raw) {
        super("Invalid person ID: '%s' — expected the UUID returned when the person was hired or enrolled".formatted(raw));
    }
}
