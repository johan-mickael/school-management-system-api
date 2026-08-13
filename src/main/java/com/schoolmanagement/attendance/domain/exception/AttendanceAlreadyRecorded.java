package com.schoolmanagement.attendance.domain.exception;

import com.schoolmanagement.scheduling.domain.SessionId;
import com.schoolmanagement.shared.domain.DomainException;
import com.schoolmanagement.student.domain.StudentId;

public final class AttendanceAlreadyRecorded extends DomainException {
    public AttendanceAlreadyRecorded(SessionId sessionId, StudentId studentId) {
        super("Student '%s' already has an attendance record for session '%s'".formatted(studentId, sessionId));
    }
}
