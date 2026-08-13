package com.schoolmanagement.attendance.presentation.dto;

import java.time.Instant;

import com.schoolmanagement.attendance.application.view.AttendanceRecordView;

public record AttendanceRecordResponse(
    String id,
    String sessionId,
    String studentId,
    String status,
    Instant signedAt,
    String justification) {

  public static AttendanceRecordResponse from(AttendanceRecordView v) {
    return new AttendanceRecordResponse(
        v.id(),
        v.sessionId(),
        v.studentId(),
        v.status(),
        v.signedAt(),
        v.justification());
  }
}
