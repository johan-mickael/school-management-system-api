package com.schoolmanagement.attendance.infrastructure.persistence;

import org.springframework.stereotype.Component;

import com.schoolmanagement.attendance.domain.AttendanceId;
import com.schoolmanagement.attendance.domain.AttendanceRecord;
import com.schoolmanagement.attendance.domain.Justification;
import com.schoolmanagement.scheduling.domain.SessionId;
import com.schoolmanagement.student.domain.StudentId;

@Component
public class AttendanceRecordMapper {

  public AttendanceRecordEntity toEntity(AttendanceRecord r) {
    return new AttendanceRecordEntity(
        r.id().value(),
        r.sessionId().value(),
        r.studentId().value(),
        r.status(),
        r.signedAt(),
        r.justification() == null ? null : r.justification().value());
  }

  public AttendanceRecord toDomain(AttendanceRecordEntity e) {
    return AttendanceRecord.reconstitute(
        new AttendanceId(e.getId()),
        new SessionId(e.getSessionId()),
        new StudentId(e.getStudentId()),
        e.getStatus(),
        e.getSignedAt(),
        e.getJustification() == null ? null : new Justification(e.getJustification()));
  }
}
