package com.schoolmanagement.attendance.domain.exception;

import com.schoolmanagement.scheduling.domain.SessionId;
import com.schoolmanagement.shared.domain.DomainException;
import com.schoolmanagement.student.domain.StudentId;

public final class AttendanceRecordNotFound extends DomainException {
    public AttendanceRecordNotFound(SessionId sessionId, StudentId studentId) {
        super("No attendance record for student '%s' in session '%s'".formatted(studentId, sessionId));
    }
}
