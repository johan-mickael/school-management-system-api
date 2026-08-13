package com.schoolmanagement.scheduling.application;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;

import com.schoolmanagement.course.domain.Coefficient;
import com.schoolmanagement.course.domain.Course;
import com.schoolmanagement.course.domain.CourseCode;
import com.schoolmanagement.course.domain.CourseId;
import com.schoolmanagement.course.domain.CourseRepository;
import com.schoolmanagement.course.domain.CourseTitle;
import com.schoolmanagement.course.domain.exception.CourseNotFound;
import com.schoolmanagement.promotion.domain.AcademicYear;
import com.schoolmanagement.promotion.domain.Capacity;
import com.schoolmanagement.promotion.domain.Promotion;
import com.schoolmanagement.promotion.domain.PromotionId;
import com.schoolmanagement.promotion.domain.PromotionName;
import com.schoolmanagement.promotion.domain.PromotionRepository;
import com.schoolmanagement.promotion.domain.exception.PromotionNotFound;
import com.schoolmanagement.scheduling.application.command.cancelsession.CancelSessionCommand;
import com.schoolmanagement.scheduling.application.command.cancelsession.CancelSessionHandler;
import com.schoolmanagement.scheduling.application.command.closesessionsigning.CloseSessionSigningCommand;
import com.schoolmanagement.scheduling.application.command.closesessionsigning.CloseSessionSigningHandler;
import com.schoolmanagement.scheduling.application.command.opensessionsigning.OpenSessionSigningCommand;
import com.schoolmanagement.scheduling.application.command.opensessionsigning.OpenSessionSigningHandler;
import com.schoolmanagement.scheduling.application.command.schedulesession.ScheduleSessionCommand;
import com.schoolmanagement.scheduling.application.command.schedulesession.ScheduleSessionHandler;
import com.schoolmanagement.scheduling.application.view.SessionView;
import com.schoolmanagement.scheduling.domain.Session;
import com.schoolmanagement.scheduling.domain.SessionId;
import com.schoolmanagement.scheduling.domain.SessionRepository;
import com.schoolmanagement.scheduling.domain.exception.InvalidSessionTransition;
import com.schoolmanagement.scheduling.domain.exception.SessionNotFound;
import com.schoolmanagement.shared.domain.exception.InvalidTimeWindow;
import com.schoolmanagement.teacher.domain.StaffNumber;
import com.schoolmanagement.teacher.domain.Teacher;
import com.schoolmanagement.teacher.domain.TeacherId;
import com.schoolmanagement.teacher.domain.TeacherRepository;
import com.schoolmanagement.teacher.domain.exception.TeacherNotFound;
import com.schoolmanagement.shared.domain.EmailAddress;
import com.schoolmanagement.shared.domain.FullName;

class SessionLifecycleHandlerTest {
  static class InMemorySessions implements SessionRepository {
    final List<Session> db = new ArrayList<>();

    public void save(Session s) {
      db.removeIf(existing -> existing.id().equals(s.id()));
      db.add(s);
    }

    public Session getById(SessionId id) {
      return db.stream()
          .filter(s -> s.id().equals(id))
          .findFirst()
          .orElseThrow(() -> new SessionNotFound(id));
    }

    public List<Session> findByPromotionId(PromotionId promotionId) {
      return db.stream().filter(s -> s.promotionId().equals(promotionId)).toList();
    }
  }

  static class InMemoryCourses implements CourseRepository {
    final Map<java.util.UUID, Course> db = new HashMap<>();

    public void save(Course c) {
      db.put(c.id().value(), c);
    }

    public Course getById(CourseId id) {
      Course c = db.get(id.value());
      if (c == null)
        throw new CourseNotFound(id);
      return c;
    }

    public List<Course> findByPromotionId(PromotionId promotionId) {
      return db.values().stream().filter(c -> c.promotionId().equals(promotionId)).toList();
    }
  }

  static class InMemoryPromotions implements PromotionRepository {
    final Map<java.util.UUID, Promotion> db = new HashMap<>();

    public void save(Promotion p) {
      db.put(p.id().value(), p);
    }

    public Promotion getById(PromotionId id) {
      Promotion p = db.get(id.value());
      if (p == null)
        throw new PromotionNotFound(id);
      return p;
    }
  }

  static class InMemoryTeachers implements TeacherRepository {
    final Map<java.util.UUID, Teacher> db = new HashMap<>();

    public void save(Teacher t) {
      db.put(t.id().value(), t);
    }

    public Teacher getById(TeacherId id) {
      Teacher t = db.get(id.value());
      if (t == null)
        throw new TeacherNotFound(id);
      return t;
    }

    public List<Teacher> findAll() {
      return List.copyOf(db.values());
    }
  }

  private final InMemorySessions sessions = new InMemorySessions();
  private final InMemoryCourses courses = new InMemoryCourses();
  private final InMemoryPromotions promotions = new InMemoryPromotions();
  private final InMemoryTeachers teachers = new InMemoryTeachers();
  private final ScheduleSessionHandler scheduleHandler =
      new ScheduleSessionHandler(sessions, courses, promotions, teachers);
  private final OpenSessionSigningHandler openHandler = new OpenSessionSigningHandler(sessions);
  private final CloseSessionSigningHandler closeHandler = new CloseSessionSigningHandler(sessions);
  private final CancelSessionHandler cancelHandler = new CancelSessionHandler(sessions);

  private Promotion aPromotion() {
    Promotion promotion = Promotion.create(
        PromotionId.generate(), new PromotionName("MSE 2025"), new AcademicYear("2025-2026"), new Capacity(30));
    promotions.save(promotion);
    return promotion;
  }

  private Teacher aTeacher() {
    Teacher teacher = Teacher.hire(
        TeacherId.generate(), new StaffNumber("TCH-2025-0001"), new FullName("Ada", "Lovelace"),
        new EmailAddress("ada@example.com"), Instant.parse("2025-09-01T08:00:00Z"));
    teachers.save(teacher);
    return teacher;
  }

  private Course aCourse(PromotionId promotionId) {
    Course course = Course.create(
        CourseId.generate(), new CourseCode("CS101"), new CourseTitle("Algorithms"),
        new Coefficient(3.0), promotionId, null);
    courses.save(course);
    return course;
  }

  private SessionView aScheduledSession() {
    Promotion promotion = aPromotion();
    Teacher teacher = aTeacher();
    Course course = aCourse(promotion.id());
    return scheduleHandler.handle(new ScheduleSessionCommand(
        course.id().toString(), promotion.id().toString(), teacher.id().toString(),
        Instant.parse("2025-09-01T08:00:00Z"), Instant.parse("2025-09-01T10:00:00Z"), 900));
  }

  @Test
  void schedules_a_session() {
    SessionView view = aScheduledSession();

    assertThat(view.status()).isEqualTo("SCHEDULED");
  }

  @Test
  void rejects_a_window_where_end_is_not_after_start() {
    Promotion promotion = aPromotion();
    Teacher teacher = aTeacher();
    Course course = aCourse(promotion.id());

    assertThatThrownBy(() -> scheduleHandler.handle(new ScheduleSessionCommand(
        course.id().toString(), promotion.id().toString(), teacher.id().toString(),
        Instant.parse("2025-09-01T10:00:00Z"), Instant.parse("2025-09-01T08:00:00Z"), 0)))
            .isInstanceOf(InvalidTimeWindow.class);
  }

  @Test
  void rejects_scheduling_against_a_nonexistent_course() {
    Promotion promotion = aPromotion();
    Teacher teacher = aTeacher();

    assertThatThrownBy(() -> scheduleHandler.handle(new ScheduleSessionCommand(
        CourseId.generate().toString(), promotion.id().toString(), teacher.id().toString(),
        Instant.parse("2025-09-01T08:00:00Z"), Instant.parse("2025-09-01T10:00:00Z"), 0)))
            .isInstanceOf(CourseNotFound.class);
  }

  @Test
  void opens_then_closes_signing() {
    SessionView scheduled = aScheduledSession();

    SessionView opened = openHandler.handle(new OpenSessionSigningCommand(scheduled.id()));
    assertThat(opened.status()).isEqualTo("SIGNING_OPEN");

    SessionView closed = closeHandler.handle(new CloseSessionSigningCommand(scheduled.id()));
    assertThat(closed.status()).isEqualTo("SIGNING_CLOSED");
  }

  @Test
  void rejects_closing_signing_that_was_never_opened() {
    SessionView scheduled = aScheduledSession();

    assertThatThrownBy(() -> closeHandler.handle(new CloseSessionSigningCommand(scheduled.id())))
        .isInstanceOf(InvalidSessionTransition.class);
  }

  @Test
  void rejects_opening_signing_twice() {
    SessionView scheduled = aScheduledSession();
    openHandler.handle(new OpenSessionSigningCommand(scheduled.id()));

    assertThatThrownBy(() -> openHandler.handle(new OpenSessionSigningCommand(scheduled.id())))
        .isInstanceOf(InvalidSessionTransition.class);
  }

  @Test
  void cancels_a_scheduled_session() {
    SessionView scheduled = aScheduledSession();

    SessionView cancelled = cancelHandler.handle(new CancelSessionCommand(scheduled.id()));

    assertThat(cancelled.status()).isEqualTo("CANCELLED");
  }

  @Test
  void rejects_reopening_a_cancelled_session() {
    SessionView scheduled = aScheduledSession();
    cancelHandler.handle(new CancelSessionCommand(scheduled.id()));

    assertThatThrownBy(() -> openHandler.handle(new OpenSessionSigningCommand(scheduled.id())))
        .isInstanceOf(InvalidSessionTransition.class);
  }

  @Test
  void rejects_cancelling_a_closed_session() {
    SessionView scheduled = aScheduledSession();
    openHandler.handle(new OpenSessionSigningCommand(scheduled.id()));
    closeHandler.handle(new CloseSessionSigningCommand(scheduled.id()));

    assertThatThrownBy(() -> cancelHandler.handle(new CancelSessionCommand(scheduled.id())))
        .isInstanceOf(InvalidSessionTransition.class);
  }
}
