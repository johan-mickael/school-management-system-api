package com.schoolmanagement.iam.domain.exception;

import com.schoolmanagement.shared.domain.DomainException;
import com.schoolmanagement.iam.domain.UserId;

public final class UserAlreadyDisabled extends DomainException {
    public UserAlreadyDisabled(UserId id) {
        super("User '%s' is already disabled".formatted(id));
    }
}
