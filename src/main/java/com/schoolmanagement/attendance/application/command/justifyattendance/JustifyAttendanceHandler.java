package com.schoolmanagement.attendance.application.command.justifyattendance;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.schoolmanagement.attendance.application.view.AttendanceRecordView;
import com.schoolmanagement.attendance.domain.AttendanceRecord;
import com.schoolmanagement.attendance.domain.AttendanceRecordRepository;
import com.schoolmanagement.attendance.domain.Justification;
import com.schoolmanagement.attendance.domain.exception.AttendanceRecordNotFound;
import com.schoolmanagement.scheduling.domain.SessionId;
import com.schoolmanagement.student.domain.StudentId;

@Service
public class JustifyAttendanceHandler {
  private final AttendanceRecordRepository attendanceRecords;

  public JustifyAttendanceHandler(AttendanceRecordRepository attendanceRecords) {
    this.attendanceRecords = attendanceRecords;
  }

  @Transactional
  public AttendanceRecordView handle(JustifyAttendanceCommand command) {
    SessionId sessionId = SessionId.of(command.sessionId());
    StudentId studentId = StudentId.of(command.studentId());

    AttendanceRecord record = attendanceRecords.findBySessionIdAndStudentId(sessionId, studentId)
        .orElseThrow(() -> new AttendanceRecordNotFound(sessionId, studentId));

    record.excuse(new Justification(command.justification()));
    attendanceRecords.save(record);

    return AttendanceRecordView.from(record);
  }
}
