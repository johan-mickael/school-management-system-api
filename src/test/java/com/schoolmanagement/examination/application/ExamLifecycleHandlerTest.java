package com.schoolmanagement.examination.application;

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
import com.schoolmanagement.examination.application.command.closeexam.CloseExamCommand;
import com.schoolmanagement.examination.application.command.closeexam.CloseExamHandler;
import com.schoolmanagement.examination.application.command.openexam.OpenExamCommand;
import com.schoolmanagement.examination.application.command.openexam.OpenExamHandler;
import com.schoolmanagement.examination.application.command.scheduleexam.ScheduleExamCommand;
import com.schoolmanagement.examination.application.command.scheduleexam.ScheduleExamHandler;
import com.schoolmanagement.examination.application.view.ExamView;
import com.schoolmanagement.examination.domain.Exam;
import com.schoolmanagement.examination.domain.ExamId;
import com.schoolmanagement.examination.domain.ExamRepository;
import com.schoolmanagement.examination.domain.exception.ExamNotFound;
import com.schoolmanagement.examination.domain.exception.InvalidExamTransition;
import com.schoolmanagement.promotion.domain.AcademicYear;
import com.schoolmanagement.promotion.domain.Capacity;
import com.schoolmanagement.promotion.domain.Promotion;
import com.schoolmanagement.promotion.domain.PromotionId;
import com.schoolmanagement.promotion.domain.PromotionName;
import com.schoolmanagement.promotion.domain.PromotionRepository;
import com.schoolmanagement.promotion.domain.exception.PromotionNotFound;
import com.schoolmanagement.shared.domain.exception.InvalidTimeWindow;

class ExamLifecycleHandlerTest {
  static class InMemoryExams implements ExamRepository {
    final List<Exam> db = new ArrayList<>();

    public void save(Exam e) {
      db.removeIf(existing -> existing.id().equals(e.id()));
      db.add(e);
    }

    public Exam getById(ExamId id) {
      return db.stream()
          .filter(e -> e.id().equals(id))
          .findFirst()
          .orElseThrow(() -> new ExamNotFound(id));
    }

    public List<Exam> findByPromotionId(PromotionId promotionId) {
      return db.stream().filter(e -> e.promotionId().equals(promotionId)).toList();
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

    public List<Promotion> findArchived() {
      return db.values().stream().filter(Promotion::isArchived).toList();
    }

    @Override
    public List<Promotion> findActive() {
      return db.values().stream().filter(p -> !p.isArchived()).toList();
    }
  }

  private final InMemoryExams exams = new InMemoryExams();
  private final InMemoryCourses courses = new InMemoryCourses();
  private final InMemoryPromotions promotions = new InMemoryPromotions();
  private final ScheduleExamHandler scheduleHandler = new ScheduleExamHandler(exams, courses, promotions);
  private final OpenExamHandler openHandler = new OpenExamHandler(exams);
  private final CloseExamHandler closeHandler = new CloseExamHandler(exams);

  private Promotion aPromotion() {
    Promotion promotion = Promotion.create(
        PromotionId.generate(), new PromotionName("MSE 2025"), new AcademicYear("2025-2026"), new Capacity(30));
    promotions.save(promotion);
    return promotion;
  }

  private Course aCourse(PromotionId promotionId) {
    Course course = Course.create(
        CourseId.generate(), new CourseCode("CS101"), new CourseTitle("Algorithms"),
        new Coefficient(3.0), promotionId, null);
    courses.save(course);
    return course;
  }

  private ExamView aScheduledExam() {
    Promotion promotion = aPromotion();
    Course course = aCourse(promotion.id());
    return scheduleHandler.handle(new ScheduleExamCommand(
        course.id().toString(), promotion.id().toString(),
        Instant.parse("2025-12-01T08:00:00Z"), Instant.parse("2025-12-01T10:00:00Z")));
  }

  @Test
  void schedules_an_exam() {
    ExamView view = aScheduledExam();

    assertThat(view.status()).isEqualTo("SCHEDULED");
  }

  @Test
  void rejects_a_window_where_end_is_not_after_start() {
    Promotion promotion = aPromotion();
    Course course = aCourse(promotion.id());

    assertThatThrownBy(() -> scheduleHandler.handle(new ScheduleExamCommand(
        course.id().toString(), promotion.id().toString(),
        Instant.parse("2025-12-01T10:00:00Z"), Instant.parse("2025-12-01T08:00:00Z"))))
            .isInstanceOf(InvalidTimeWindow.class);
  }

  @Test
  void opens_then_closes_an_exam() {
    ExamView scheduled = aScheduledExam();

    ExamView opened = openHandler.handle(new OpenExamCommand(scheduled.id()));
    assertThat(opened.status()).isEqualTo("OPEN");

    ExamView closed = closeHandler.handle(new CloseExamCommand(scheduled.id()));
    assertThat(closed.status()).isEqualTo("CLOSED");
  }

  @Test
  void rejects_closing_an_exam_that_was_never_opened() {
    ExamView scheduled = aScheduledExam();

    assertThatThrownBy(() -> closeHandler.handle(new CloseExamCommand(scheduled.id())))
        .isInstanceOf(InvalidExamTransition.class);
  }

  @Test
  void rejects_opening_an_exam_twice() {
    ExamView scheduled = aScheduledExam();
    openHandler.handle(new OpenExamCommand(scheduled.id()));

    assertThatThrownBy(() -> openHandler.handle(new OpenExamCommand(scheduled.id())))
        .isInstanceOf(InvalidExamTransition.class);
  }

  @Test
  void rejects_reopening_a_closed_exam() {
    ExamView scheduled = aScheduledExam();
    openHandler.handle(new OpenExamCommand(scheduled.id()));
    closeHandler.handle(new CloseExamCommand(scheduled.id()));

    assertThatThrownBy(() -> openHandler.handle(new OpenExamCommand(scheduled.id())))
        .isInstanceOf(InvalidExamTransition.class);
  }
}
