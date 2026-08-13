package com.schoolmanagement.teacher.domain.exception;

import com.schoolmanagement.shared.domain.DomainException;

public final class InvalidStaffNumber extends DomainException {
    public InvalidStaffNumber(String raw) {
        super("Invalid staff number: '%s'".formatted(raw));
    }
}
