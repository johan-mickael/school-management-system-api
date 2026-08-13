package com.schoolmanagement.examination.application;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;

import com.schoolmanagement.course.domain.CourseId;
import com.schoolmanagement.examination.application.command.recordintegrityevent.RecordIntegrityEventCommand;
import com.schoolmanagement.examination.application.command.recordintegrityevent.RecordIntegrityEventHandler;
import com.schoolmanagement.examination.application.command.startattempt.StartAttemptCommand;
import com.schoolmanagement.examination.application.command.startattempt.StartAttemptHandler;
import com.schoolmanagement.examination.application.command.submitattempt.SubmitAttemptCommand;
import com.schoolmanagement.examination.application.command.submitattempt.SubmitAttemptHandler;
import com.schoolmanagement.examination.application.view.ExamAttemptView;
import com.schoolmanagement.examination.domain.AttemptId;
import com.schoolmanagement.examination.domain.Exam;
import com.schoolmanagement.examination.domain.ExamAttempt;
import com.schoolmanagement.examination.domain.ExamAttemptRepository;
import com.schoolmanagement.examination.domain.ExamId;
import com.schoolmanagement.examination.domain.ExamRepository;
import com.schoolmanagement.examination.domain.exception.AttemptAlreadyExists;
import com.schoolmanagement.examination.domain.exception.AttemptNotFound;
import com.schoolmanagement.examination.domain.exception.ExamNotOpenForAttempt;
import com.schoolmanagement.examination.domain.exception.InvalidAttemptTransition;
import com.schoolmanagement.iam.domain.PasswordHash;
import com.schoolmanagement.iam.domain.PersonId;
import com.schoolmanagement.iam.domain.Role;
import com.schoolmanagement.iam.domain.User;
import com.schoolmanagement.iam.domain.UserId;
import com.schoolmanagement.iam.domain.UserRepository;
import com.schoolmanagement.iam.domain.Username;
import com.schoolmanagement.promotion.domain.PromotionId;
import com.schoolmanagement.shared.domain.EmailAddress;
import com.schoolmanagement.shared.domain.FullName;
import com.schoolmanagement.shared.domain.TimeWindow;
import com.schoolmanagement.student.domain.Student;
import com.schoolmanagement.student.domain.StudentId;
import com.schoolmanagement.student.domain.StudentNumber;
import com.schoolmanagement.student.domain.StudentRepository;
import com.schoolmanagement.student.domain.exception.StudentAlreadyArchived;
import com.schoolmanagement.student.domain.exception.StudentNotFound;

class ExamAttemptLifecycleHandlerTest {
  static class InMemoryAttempts implements ExamAttemptRepository {
    final Map<java.util.UUID, ExamAttempt> db = new HashMap<>();

    public void save(ExamAttempt a) {
      db.put(a.id().value(), a);
    }

    public ExamAttempt getById(AttemptId id) {
      ExamAttempt a = db.get(id.value());
      if (a == null)
        throw new AttemptNotFound(id);
      return a;
    }

    public Optional<ExamAttempt> findByExamIdAndStudentId(ExamId examId, StudentId studentId) {
      return db.values().stream()
          .filter(a -> a.examId().equals(examId) && a.studentId().equals(studentId))
          .findFirst();
    }

    public List<ExamAttempt> findByExamId(ExamId examId) {
      return db.values().stream().filter(a -> a.examId().equals(examId)).toList();
    }
  }

  static class InMemoryExams implements ExamRepository {
    final Map<java.util.UUID, Exam> db = new HashMap<>();

    public void save(Exam e) {
      db.put(e.id().value(), e);
    }

    public Exam getById(ExamId id) {
      return db.get(id.value());
    }

    public List<Exam> findByPromotionId(PromotionId promotionId) {
      return db.values().stream().filter(e -> e.promotionId().equals(promotionId)).toList();
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

  static class InMemoryStudents implements StudentRepository {
    final Map<java.util.UUID, Student> db = new HashMap<>();

    public void save(Student s) {
      db.put(s.id().value(), s);
    }

    public Student getById(StudentId id) {
      Student s = db.get(id.value());
      if (s == null)
        throw new StudentNotFound(id);
      return s;
    }

    public List<Student> findByPromotionId(PromotionId promotionId) {
      return db.values().stream().filter(s -> promotionId.equals(s.promotionId())).toList();
    }
  }

  private final InMemoryAttempts attempts = new InMemoryAttempts();
  private final InMemoryExams exams = new InMemoryExams();
  private final InMemoryUsers users = new InMemoryUsers();
  private final InMemoryStudents students = new InMemoryStudents();
  private final Clock clock = Clock.fixed(Instant.parse("2025-12-01T09:00:00Z"), ZoneOffset.UTC);
  private final StartAttemptHandler startHandler = new StartAttemptHandler(attempts, exams, users, students);
  private final SubmitAttemptHandler submitHandler = new SubmitAttemptHandler(attempts);
  private final java.util.concurrent.atomic.AtomicInteger studentNumberSequence =
      new java.util.concurrent.atomic.AtomicInteger();

  private RecordIntegrityEventHandler recordHandler(int threshold) {
    return new RecordIntegrityEventHandler(attempts, clock, threshold);
  }

  private Exam anOpenExam() {
    Exam exam = Exam.schedule(
        ExamId.generate(), CourseId.generate(), PromotionId.generate(),
        new TimeWindow(Instant.parse("2025-12-01T08:00:00Z"), Instant.parse("2025-12-01T10:00:00Z")));
    exam.open();
    exams.save(exam);
    return exam;
  }

  private User aStudentUser(StudentId studentId) {
    User user = User.register(
        UserId.generate(), new Username("student" + studentId.value()), new PasswordHash("hashed:x"),
        Role.STUDENT, new PersonId(studentId.value()));
    users.save(user);
    students.save(Student.enroll(
        studentId, new StudentNumber("STU-2025-%04d".formatted(studentNumberSequence.incrementAndGet())),
        new FullName("Ada", "Lovelace"), new EmailAddress(studentId.value() + "@example.com"),
        Instant.parse("2025-09-01T00:00:00Z")));
    return user;
  }

  @Test
  void starts_an_attempt_on_an_open_exam() {
    Exam exam = anOpenExam();
    User user = aStudentUser(StudentId.generate());

    ExamAttemptView view = startHandler.handle(new StartAttemptCommand(exam.id().toString(), user.id().toString()));

    assertThat(view.status()).isEqualTo("IN_PROGRESS");
  }

  @Test
  void rejects_starting_an_attempt_on_a_non_open_exam() {
    Exam exam = Exam.schedule(
        ExamId.generate(), CourseId.generate(), PromotionId.generate(),
        new TimeWindow(Instant.parse("2025-12-01T08:00:00Z"), Instant.parse("2025-12-01T10:00:00Z")));
    exams.save(exam);
    User user = aStudentUser(StudentId.generate());

    assertThatThrownBy(() -> startHandler.handle(new StartAttemptCommand(exam.id().toString(), user.id().toString())))
        .isInstanceOf(ExamNotOpenForAttempt.class);
  }

  @Test
  void rejects_starting_an_attempt_for_an_archived_student() {
    Exam exam = anOpenExam();
    User user = aStudentUser(StudentId.generate());
    Student student = students.getById(new StudentId(user.personId().value()));
    student.archive();
    students.save(student);

    assertThatThrownBy(() -> startHandler.handle(new StartAttemptCommand(exam.id().toString(), user.id().toString())))
        .isInstanceOf(StudentAlreadyArchived.class);
  }

  @Test
  void rejects_a_second_attempt_for_the_same_exam_and_student() {
    Exam exam = anOpenExam();
    User user = aStudentUser(StudentId.generate());
    startHandler.handle(new StartAttemptCommand(exam.id().toString(), user.id().toString()));

    assertThatThrownBy(() -> startHandler.handle(new StartAttemptCommand(exam.id().toString(), user.id().toString())))
        .isInstanceOf(AttemptAlreadyExists.class);
  }

  @Test
  void flags_an_attempt_once_the_integrity_threshold_is_reached() {
    Exam exam = anOpenExam();
    User user = aStudentUser(StudentId.generate());
    ExamAttemptView started = startHandler.handle(new StartAttemptCommand(exam.id().toString(), user.id().toString()));
    RecordIntegrityEventHandler recordHandler = recordHandler(3);

    recordHandler.handle(new RecordIntegrityEventCommand(started.id(), "TAB_SWITCH"));
    recordHandler.handle(new RecordIntegrityEventCommand(started.id(), "WINDOW_BLUR"));
    ExamAttemptView third = recordHandler.handle(new RecordIntegrityEventCommand(started.id(), "COPY"));

    assertThat(third.integrityScore()).isEqualTo(3);
    assertThat(third.status()).isEqualTo("FLAGGED");
  }

  @Test
  void stays_in_progress_below_the_threshold() {
    Exam exam = anOpenExam();
    User user = aStudentUser(StudentId.generate());
    ExamAttemptView started = startHandler.handle(new StartAttemptCommand(exam.id().toString(), user.id().toString()));
    RecordIntegrityEventHandler recordHandler = recordHandler(5);

    ExamAttemptView after = recordHandler.handle(new RecordIntegrityEventCommand(started.id(), "TAB_SWITCH"));

    assertThat(after.status()).isEqualTo("IN_PROGRESS");
  }

  @Test
  void rejects_recording_an_event_after_submission() {
    Exam exam = anOpenExam();
    User user = aStudentUser(StudentId.generate());
    ExamAttemptView started = startHandler.handle(new StartAttemptCommand(exam.id().toString(), user.id().toString()));
    submitHandler.handle(new SubmitAttemptCommand(started.id()));
    RecordIntegrityEventHandler recordHandler = recordHandler(5);

    assertThatThrownBy(() -> recordHandler.handle(new RecordIntegrityEventCommand(started.id(), "TAB_SWITCH")))
        .isInstanceOf(InvalidAttemptTransition.class);
  }

  @Test
  void rejects_submitting_twice() {
    Exam exam = anOpenExam();
    User user = aStudentUser(StudentId.generate());
    ExamAttemptView started = startHandler.handle(new StartAttemptCommand(exam.id().toString(), user.id().toString()));
    submitHandler.handle(new SubmitAttemptCommand(started.id()));

    assertThatThrownBy(() -> submitHandler.handle(new SubmitAttemptCommand(started.id())))
        .isInstanceOf(InvalidAttemptTransition.class);
  }
}
