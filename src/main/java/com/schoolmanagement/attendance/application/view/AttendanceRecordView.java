package com.schoolmanagement.attendance.application.view;

import java.time.Instant;

import com.schoolmanagement.attendance.domain.AttendanceRecord;

public record AttendanceRecordView(
    String id,
    String sessionId,
    String studentId,
    String status,
    Instant signedAt,
    String justification) {

    public static AttendanceRecordView from(AttendanceRecord r) {
        return new AttendanceRecordView(
                r.id().toString(),
                r.sessionId().toString(),
                r.studentId().toString(),
                r.status().name(),
                r.signedAt(),
                r.justification() == null ? null : r.justification().value());
    }
}
