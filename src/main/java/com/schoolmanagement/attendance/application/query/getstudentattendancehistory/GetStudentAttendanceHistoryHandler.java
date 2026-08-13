package com.schoolmanagement.attendance.application.query.getstudentattendancehistory;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.schoolmanagement.attendance.application.view.AttendanceRecordView;
import com.schoolmanagement.attendance.domain.AttendanceRecordRepository;
import com.schoolmanagement.student.domain.StudentId;

@Service
public class GetStudentAttendanceHistoryHandler {
  private final AttendanceRecordRepository attendanceRecords;

  public GetStudentAttendanceHistoryHandler(AttendanceRecordRepository attendanceRecords) {
    this.attendanceRecords = attendanceRecords;
  }

  @Transactional(readOnly = true)
  public List<AttendanceRecordView> handle(GetStudentAttendanceHistoryQuery query) {
    return attendanceRecords.findByStudentId(StudentId.of(query.studentId())).stream()
        .map(AttendanceRecordView::from)
        .toList();
  }
}
