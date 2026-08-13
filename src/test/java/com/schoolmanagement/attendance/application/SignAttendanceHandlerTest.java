package com.schoolmanagement.attendance.application;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;

import com.schoolmanagement.attendance.application.command.justifyattendance.JustifyAttendanceCommand;
import com.schoolmanagement.attendance.application.command.justifyattendance.JustifyAttendanceHandler;
import com.schoolmanagement.attendance.application.command.signattendance.SignAttendanceCommand;
import com.schoolmanagement.attendance.application.command.signattendance.SignAttendanceHandler;
import com.schoolmanagement.attendance.application.view.AttendanceRecordView;
import com.schoolmanagement.attendance.domain.AttendanceRecord;
import com.schoolmanagement.attendance.domain.AttendanceRecordRepository;
import com.schoolmanagement.attendance.domain.exception.AttendanceAlreadyRecorded;
import com.schoolmanagement.attendance.domain.exception.AttendanceRecordNotFound;
import com.schoolmanagement.attendance.domain.exception.InvalidAttendanceTransition;
import com.schoolmanagement.attendance.domain.exception.SessionSigningNotOpen;
import com.schoolmanagement.course.domain.CourseId;
import com.schoolmanagement.iam.domain.PasswordHash;
import com.schoolmanagement.iam.domain.PersonId;
import com.schoolmanagement.iam.domain.Role;
import com.schoolmanagement.iam.domain.User;
import com.schoolmanagement.iam.domain.UserId;
import com.schoolmanagement.iam.domain.UserRepository;
import com.schoolmanagement.iam.domain.Username;
import com.schoolmanagement.promotion.domain.PromotionId;
import com.schoolmanagement.scheduling.domain.GracePeriod;
import com.schoolmanagement.scheduling.domain.Session;
import com.schoolmanagement.scheduling.domain.SessionId;
import com.schoolmanagement.scheduling.domain.SessionRepository;
import com.schoolmanagement.scheduling.domain.exception.SessionNotFound;
import com.schoolmanagement.shared.domain.TimeWindow;
import com.schoolmanagement.student.domain.StudentId;
import com.schoolmanagement.teacher.domain.TeacherId;

class SignAttendanceHandlerTest {
  static class InMemorySessions implements SessionRepository {
    final Map<java.util.UUID, Session> db = new HashMap<>();

    public void save(Session s) {
      db.put(s.id().value(), s);
    }

    public Session getById(SessionId id) {
      Session s = db.get(id.value());
      if (s == null)
        throw new SessionNotFound(id);
      return s;
    }

    public List<Session> findByPromotionId(PromotionId promotionId) {
      return db.values().stream().filter(s -> s.promotionId().equals(promotionId)).toList();
    }
  }

  static class InMemoryUsers implements UserRepository {
    final Map<java.util.UUID, User> db = new HashMap<>();

    public void save(User u) {
      db.put(u.id().value(), u);
    }

    public Optional<User> findByUsername(Username username) {
      return db.values().stream().filter(u -> u.username().equals(username)).findFirst();
    }

    public Optional<User> findById(UserId id) {
      return Optional.ofNullable(db.get(id.value()));
    }
  }

  static class InMemoryAttendanceRecords implements AttendanceRecordRepository {
    final List<AttendanceRecord> db = new ArrayList<>();

    public void save(AttendanceRecord r) {
      db.removeIf(existing -> existing.id().equals(r.id()));
      db.add(r);
    }

    public Optional<AttendanceRecord> findBySessionIdAndStudentId(SessionId sessionId, StudentId studentId) {
      return db.stream()
          .filter(r -> r.sessionId().equals(sessionId) && r.studentId().equals(studentId))
          .findFirst();
    }

    public List<AttendanceRecord> findBySessionId(SessionId sessionId) {
      return db.stream().filter(r -> r.sessionId().equals(sessionId)).toList();
    }

    public List<AttendanceRecord> findByStudentId(StudentId studentId) {
      return db.stream().filter(r -> r.studentId().equals(studentId)).toList();
    }
  }

  private final InMemorySessions sessions = new InMemorySessions();
  private final InMemoryUsers users = new InMemoryUsers();
  private final InMemoryAttendanceRecords attendanceRecords = new InMemoryAttendanceRecords();
  private final Clock clock = Clock.fixed(Instant.parse("2025-09-01T08:10:00Z"), ZoneOffset.UTC);
  private final SignAttendanceHandler signHandler =
      new SignAttendanceHandler(attendanceRecords, sessions, users, clock);
  private final JustifyAttendanceHandler justifyHandler = new JustifyAttendanceHandler(attendanceRecords);

  private Session openSession(Instant start, Duration gracePeriod) {
    Session session = Session.schedule(
        SessionId.generate(), CourseId.generate(), PromotionId.generate(), TeacherId.generate(),
        new TimeWindow(start, start.plus(Duration.ofHours(2))), new GracePeriod(gracePeriod));
    session.openSigning();
    sessions.save(session);
    return session;
  }

  private User aStudentUser(StudentId studentId) {
    User user = User.register(
        UserId.generate(), new Username("student" + studentId.value()), new PasswordHash("hashed:x"),
        Role.STUDENT, new PersonId(studentId.value()));
    users.save(user);
    return user;
  }

  @Test
  void signing_within_grace_marks_present() {
    Session session = openSession(Instant.parse("2025-09-01T08:00:00Z"), Duration.ofMinutes(15));
    User user = aStudentUser(StudentId.generate());

    AttendanceRecordView view = signHandler.handle(
        new SignAttendanceCommand(session.id().toString(), user.id().toString()));

    assertThat(view.status()).isEqualTo("PRESENT");
  }

  @Test
  void signing_after_grace_marks_late() {
    Session session = openSession(Instant.parse("2025-09-01T07:00:00Z"), Duration.ofMinutes(5));
    User user = aStudentUser(StudentId.generate());

    AttendanceRecordView view = signHandler.handle(
        new SignAttendanceCommand(session.id().toString(), user.id().toString()));

    assertThat(view.status()).isEqualTo("LATE");
  }

  @Test
  void rejects_signing_when_session_is_not_open() {
    Session session = Session.schedule(
        SessionId.generate(), CourseId.generate(), PromotionId.generate(), TeacherId.generate(),
        new TimeWindow(Instant.parse("2025-09-01T08:00:00Z"), Instant.parse("2025-09-01T10:00:00Z")),
        new GracePeriod(Duration.ofMinutes(15)));
    sessions.save(session);
    User user = aStudentUser(StudentId.generate());

    assertThatThrownBy(() -> signHandler.handle(
        new SignAttendanceCommand(session.id().toString(), user.id().toString())))
            .isInstanceOf(SessionSigningNotOpen.class);
  }

  @Test
  void rejects_signing_twice_for_the_same_session() {
    Session session = openSession(Instant.parse("2025-09-01T08:00:00Z"), Duration.ofMinutes(15));
    User user = aStudentUser(StudentId.generate());
    signHandler.handle(new SignAttendanceCommand(session.id().toString(), user.id().toString()));

    assertThatThrownBy(() -> signHandler.handle(
        new SignAttendanceCommand(session.id().toString(), user.id().toString())))
            .isInstanceOf(AttendanceAlreadyRecorded.class);
  }

  @Test
  void justifies_an_absence() {
    Session session = openSession(Instant.parse("2025-09-01T08:00:00Z"), Duration.ofMinutes(15));
    StudentId studentId = StudentId.generate();
    attendanceRecords.save(com.schoolmanagement.attendance.domain.AttendanceRecord.absent(
        com.schoolmanagement.attendance.domain.AttendanceId.generate(), session.id(), studentId));

    AttendanceRecordView view = justifyHandler.handle(
        new JustifyAttendanceCommand(session.id().toString(), studentId.toString(), "Doctor's appointment"));

    assertThat(view.status()).isEqualTo("EXCUSED");
    assertThat(view.justification()).isEqualTo("Doctor's appointment");
  }

  @Test
  void rejects_justifying_a_record_that_is_not_absent() {
    Session session = openSession(Instant.parse("2025-09-01T08:00:00Z"), Duration.ofMinutes(15));
    User user = aStudentUser(StudentId.generate());
    signHandler.handle(new SignAttendanceCommand(session.id().toString(), user.id().toString()));
    StudentId studentId = new StudentId(user.personId().value());

    assertThatThrownBy(() -> justifyHandler.handle(
        new JustifyAttendanceCommand(session.id().toString(), studentId.toString(), "N/A")))
            .isInstanceOf(InvalidAttendanceTransition.class);
  }

  @Test
  void rejects_justifying_a_record_that_does_not_exist() {
    assertThatThrownBy(() -> justifyHandler.handle(
        new JustifyAttendanceCommand(SessionId.generate().toString(), StudentId.generate().toString(), "N/A")))
            .isInstanceOf(AttendanceRecordNotFound.class);
  }
}
