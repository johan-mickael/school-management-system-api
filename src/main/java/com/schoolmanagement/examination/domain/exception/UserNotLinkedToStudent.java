package com.schoolmanagement.examination.domain.exception;

import com.schoolmanagement.shared.domain.DomainException;

public final class UserNotLinkedToStudent extends DomainException {
    public UserNotLinkedToStudent(String userId) {
        super("User '%s' is not linked to a student".formatted(userId));
    }
}
