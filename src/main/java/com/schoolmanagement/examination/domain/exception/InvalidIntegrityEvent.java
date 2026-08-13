package com.schoolmanagement.examination.domain.exception;

import com.schoolmanagement.shared.domain.DomainException;

public final class InvalidIntegrityEvent extends DomainException {
    public InvalidIntegrityEvent() {
        super("Integrity event requires a type and an occurred-at instant");
    }
}
