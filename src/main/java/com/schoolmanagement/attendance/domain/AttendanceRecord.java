package com.schoolmanagement.attendance.domain;

import java.time.Instant;
import java.util.Objects;

import com.schoolmanagement.attendance.domain.exception.InvalidAttendanceTransition;
import com.schoolmanagement.scheduling.domain.SessionId;
import com.schoolmanagement.student.domain.StudentId;

public final class AttendanceRecord {
    private final AttendanceId id;
    private final SessionId sessionId;
    private final StudentId studentId;
    private AttendanceStatus status;
    private final Instant signedAt;
    private Justification justification;

    private AttendanceRecord(AttendanceId id,
            SessionId sessionId,
            StudentId studentId,
            AttendanceStatus status,
            Instant signedAt,
            Justification justification) {
        this.id = id;
        this.sessionId = sessionId;
        this.studentId = studentId;
        this.status = status;
        this.signedAt = signedAt;
        this.justification = justification;
    }

    public static AttendanceRecord sign(AttendanceId id,
            SessionId sessionId,
            StudentId studentId,
            Instant signedAt,
            Instant sessionStart,
            java.time.Duration gracePeriod) {
        AttendanceStatus status = signedAt.isAfter(sessionStart.plus(gracePeriod))
                ? AttendanceStatus.LATE
                : AttendanceStatus.PRESENT;
        return new AttendanceRecord(id, sessionId, studentId, status, signedAt, null);
    }

    public static AttendanceRecord absent(AttendanceId id, SessionId sessionId, StudentId studentId) {
        return new AttendanceRecord(id, sessionId, studentId, AttendanceStatus.ABSENT, null, null);
    }

    public static AttendanceRecord reconstitute(AttendanceId id,
            SessionId sessionId,
            StudentId studentId,
            AttendanceStatus status,
            Instant signedAt,
            Justification justification) {
        return new AttendanceRecord(id, sessionId, studentId, status, signedAt, justification);
    }

    public void excuse(Justification justification) {
        if (status != AttendanceStatus.ABSENT) {
            throw new InvalidAttendanceTransition(id, status, "excuse");
        }
        this.status = AttendanceStatus.EXCUSED;
        this.justification = justification;
    }

    public AttendanceId id() {
        return id;
    }

    public SessionId sessionId() {
        return sessionId;
    }

    public StudentId studentId() {
        return studentId;
    }

    public AttendanceStatus status() {
        return status;
    }

    public Instant signedAt() {
        return signedAt;
    }

    public Justification justification() {
        return justification;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof AttendanceRecord other))
            return false;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
