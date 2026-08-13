package com.schoolmanagement.attendance.application.query.getsessionattendance;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.schoolmanagement.attendance.application.view.AttendanceRecordView;
import com.schoolmanagement.attendance.domain.AttendanceRecordRepository;
import com.schoolmanagement.scheduling.domain.SessionId;

@Service
public class GetSessionAttendanceHandler {
  private final AttendanceRecordRepository attendanceRecords;

  public GetSessionAttendanceHandler(AttendanceRecordRepository attendanceRecords) {
    this.attendanceRecords = attendanceRecords;
  }

  @Transactional(readOnly = true)
  public List<AttendanceRecordView> handle(GetSessionAttendanceQuery query) {
    return attendanceRecords.findBySessionId(SessionId.of(query.sessionId())).stream()
        .map(AttendanceRecordView::from)
        .toList();
  }
}
