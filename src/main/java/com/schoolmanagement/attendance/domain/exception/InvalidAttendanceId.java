package com.schoolmanagement.attendance.domain.exception;

import com.schoolmanagement.shared.domain.DomainException;

public final class InvalidAttendanceId extends DomainException {
    public InvalidAttendanceId(String raw) {
        super("Invalid attendance id: '%s'".formatted(raw));
    }
}
