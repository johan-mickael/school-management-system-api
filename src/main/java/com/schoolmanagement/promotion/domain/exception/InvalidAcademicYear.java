package com.schoolmanagement.promotion.domain.exception;

import com.schoolmanagement.shared.domain.DomainException;

public final class InvalidAcademicYear extends DomainException {
    public InvalidAcademicYear(String raw) {
        super("Invalid academic year: '%s'. Expected format 'YYYY-YYYY' with consecutive years".formatted(raw));
    }
}
