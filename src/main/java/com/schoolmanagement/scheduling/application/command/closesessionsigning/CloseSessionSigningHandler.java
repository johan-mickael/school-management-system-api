package com.schoolmanagement.scheduling.application.command.closesessionsigning;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.schoolmanagement.attendance.domain.AttendanceId;
import com.schoolmanagement.attendance.domain.AttendanceRecord;
import com.schoolmanagement.attendance.domain.AttendanceRecordRepository;
import com.schoolmanagement.scheduling.application.view.SessionView;
import com.schoolmanagement.scheduling.domain.Session;
import com.schoolmanagement.scheduling.domain.SessionId;
import com.schoolmanagement.scheduling.domain.SessionRepository;
import com.schoolmanagement.student.domain.Student;
import com.schoolmanagement.student.domain.StudentRepository;

@Service
public class CloseSessionSigningHandler {
  private final SessionRepository sessions;
  private final StudentRepository students;
  private final AttendanceRecordRepository attendanceRecords;

  public CloseSessionSigningHandler(
      SessionRepository sessions,
      StudentRepository students,
      AttendanceRecordRepository attendanceRecords) {
    this.sessions = sessions;
    this.students = students;
    this.attendanceRecords = attendanceRecords;
  }

  @Transactional
  public SessionView handle(CloseSessionSigningCommand command) {
    Session session = sessions.getById(SessionId.of(command.sessionId()));

    session.closeSigning();
    sessions.save(session);

    generateAbsencesForNonSigners(session);

    return SessionView.from(session);
  }

  private void generateAbsencesForNonSigners(Session session) {
    for (Student student : students.findByPromotionId(session.promotionId())) {
      boolean alreadyRecorded = attendanceRecords
          .findBySessionIdAndStudentId(session.id(), student.id())
          .isPresent();
      if (!alreadyRecorded) {
        attendanceRecords.save(AttendanceRecord.absent(AttendanceId.generate(), session.id(), student.id()));
      }
    }
  }
}
