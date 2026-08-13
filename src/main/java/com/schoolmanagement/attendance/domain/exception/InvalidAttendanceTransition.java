package com.schoolmanagement.attendance.domain.exception;

import com.schoolmanagement.attendance.domain.AttendanceId;
import com.schoolmanagement.attendance.domain.AttendanceStatus;
import com.schoolmanagement.shared.domain.DomainException;

public final class InvalidAttendanceTransition extends DomainException {
    public InvalidAttendanceTransition(AttendanceId id, AttendanceStatus currentStatus, String action) {
        super("Cannot %s attendance record '%s' from status '%s'".formatted(action, id, currentStatus));
    }
}
