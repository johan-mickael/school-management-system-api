package com.schoolmanagement.attendance.application.command.signattendance;

import java.time.Clock;
import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.schoolmanagement.attendance.application.view.AttendanceRecordView;
import com.schoolmanagement.attendance.domain.AttendanceId;
import com.schoolmanagement.attendance.domain.AttendanceRecord;
import com.schoolmanagement.attendance.domain.AttendanceRecordRepository;
import com.schoolmanagement.attendance.domain.exception.AttendanceAlreadyRecorded;
import com.schoolmanagement.attendance.domain.exception.SessionSigningNotOpen;
import com.schoolmanagement.attendance.domain.exception.UserNotLinkedToStudent;
import com.schoolmanagement.iam.domain.User;
import com.schoolmanagement.iam.domain.UserId;
import com.schoolmanagement.iam.domain.UserRepository;
import com.schoolmanagement.scheduling.domain.Session;
import com.schoolmanagement.scheduling.domain.SessionId;
import com.schoolmanagement.scheduling.domain.SessionRepository;
import com.schoolmanagement.scheduling.domain.SessionStatus;
import com.schoolmanagement.student.domain.StudentId;

@Service
public class SignAttendanceHandler {
  private final AttendanceRecordRepository attendanceRecords;
  private final SessionRepository sessions;
  private final UserRepository users;
  private final Clock clock;

  public SignAttendanceHandler(
      AttendanceRecordRepository attendanceRecords,
      SessionRepository sessions,
      UserRepository users,
      Clock clock) {
    this.attendanceRecords = attendanceRecords;
    this.sessions = sessions;
    this.users = users;
    this.clock = clock;
  }

  @Transactional
  public AttendanceRecordView handle(SignAttendanceCommand command) {
    SessionId sessionId = SessionId.of(command.sessionId());
    Session session = sessions.getById(sessionId);
    if (session.status() != SessionStatus.SIGNING_OPEN) {
      throw new SessionSigningNotOpen(sessionId);
    }

    User user = users.findById(UserId.of(command.currentUserId()))
        .orElseThrow(() -> new UserNotLinkedToStudent(command.currentUserId()));
    if (user.personId() == null) {
      throw new UserNotLinkedToStudent(command.currentUserId());
    }
    StudentId studentId = new StudentId(user.personId().value());

    if (attendanceRecords.findBySessionIdAndStudentId(sessionId, studentId).isPresent()) {
      throw new AttendanceAlreadyRecorded(sessionId, studentId);
    }

    AttendanceRecord record = AttendanceRecord.sign(
        AttendanceId.generate(),
        sessionId,
        studentId,
        Instant.now(clock),
        session.timeWindow().start(),
        session.gracePeriod().value());

    attendanceRecords.save(record);

    return AttendanceRecordView.from(record);
  }
}
