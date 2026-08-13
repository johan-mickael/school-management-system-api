package com.schoolmanagement.iam.domain.exception;

import com.schoolmanagement.shared.domain.DomainException;
import com.schoolmanagement.iam.domain.Username;

public final class UsernameAlreadyTaken extends DomainException {
    public UsernameAlreadyTaken(Username username) {
        super("Username '%s' is already taken".formatted(username.value()));
    }
}
