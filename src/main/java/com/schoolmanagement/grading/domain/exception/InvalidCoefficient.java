package com.schoolmanagement.grading.domain.exception;

import com.schoolmanagement.shared.domain.DomainException;

public final class InvalidCoefficient extends DomainException {
    public InvalidCoefficient(double raw) {
        super("Invalid coefficient: '%s'. Coefficient must be greater than zero".formatted(raw));
    }
}
