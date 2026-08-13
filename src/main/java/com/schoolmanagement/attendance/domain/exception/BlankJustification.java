package com.schoolmanagement.attendance.domain.exception;

import com.schoolmanagement.shared.domain.DomainException;

public final class BlankJustification extends DomainException {
    public BlankJustification() {
        super("Justification must not be blank");
    }
}
