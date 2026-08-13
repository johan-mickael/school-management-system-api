package com.schoolmanagement.course.application;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;

import com.schoolmanagement.course.application.command.assignteachertocourse.AssignTeacherToCourseCommand;
import com.schoolmanagement.course.application.command.assignteachertocourse.AssignTeacherToCourseHandler;
import com.schoolmanagement.course.application.command.createcourse.CreateCourseCommand;
import com.schoolmanagement.course.application.command.createcourse.CreateCourseHandler;
import com.schoolmanagement.course.application.view.CourseView;
import com.schoolmanagement.course.domain.Course;
import com.schoolmanagement.course.domain.CourseId;
import com.schoolmanagement.course.domain.CourseRepository;
import com.schoolmanagement.course.domain.exception.CourseNotFound;
import com.schoolmanagement.course.domain.exception.InvalidCoefficient;
import com.schoolmanagement.promotion.domain.AcademicYear;
import com.schoolmanagement.promotion.domain.Capacity;
import com.schoolmanagement.promotion.domain.Promotion;
import com.schoolmanagement.promotion.domain.PromotionId;
import com.schoolmanagement.promotion.domain.PromotionName;
import com.schoolmanagement.promotion.domain.PromotionRepository;
import com.schoolmanagement.promotion.domain.exception.PromotionNotFound;
import com.schoolmanagement.shared.domain.EmailAddress;
import com.schoolmanagement.shared.domain.FullName;
import com.schoolmanagement.teacher.domain.StaffNumber;
import com.schoolmanagement.teacher.domain.Teacher;
import com.schoolmanagement.teacher.domain.TeacherId;
import com.schoolmanagement.teacher.domain.TeacherRepository;
import com.schoolmanagement.teacher.domain.exception.TeacherNotFound;

class CreateCourseHandlerTest {
  static class InMemoryCourses implements CourseRepository {
    final List<Course> db = new ArrayList<>();

    public void save(Course c) {
      db.removeIf(existing -> existing.id().equals(c.id()));
      db.add(c);
    }

    public Course getById(CourseId id) {
      return db.stream()
          .filter(c -> c.id().equals(id))
          .findFirst()
          .orElseThrow(() -> new CourseNotFound(id));
    }

    public List<Course> findByPromotionId(PromotionId promotionId) {
      return db.stream().filter(c -> c.promotionId().equals(promotionId)).toList();
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

  private final InMemoryCourses courses = new InMemoryCourses();
  private final InMemoryPromotions promotions = new InMemoryPromotions();
  private final InMemoryTeachers teachers = new InMemoryTeachers();
  private final CreateCourseHandler createHandler = new CreateCourseHandler(courses, promotions, teachers);
  private final AssignTeacherToCourseHandler assignHandler = new AssignTeacherToCourseHandler(courses, teachers);

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

  @Test
  void creates_a_course_without_a_teacher() {
    Promotion promotion = aPromotion();

    CourseView view = createHandler.handle(
        new CreateCourseCommand("CS101", "Algorithms", 3.0, promotion.id().toString(), null));

    assertThat(view.teacherId()).isNull();
    assertThat(view.promotionId()).isEqualTo(promotion.id().toString());
  }

  @Test
  void rejects_a_nonpositive_coefficient() {
    Promotion promotion = aPromotion();

    assertThatThrownBy(() -> createHandler.handle(
        new CreateCourseCommand("CS101", "Algorithms", 0.0, promotion.id().toString(), null)))
            .isInstanceOf(InvalidCoefficient.class);
  }

  @Test
  void rejects_creation_for_a_nonexistent_promotion() {
    assertThatThrownBy(() -> createHandler.handle(
        new CreateCourseCommand("CS101", "Algorithms", 3.0, PromotionId.generate().toString(), null)))
            .isInstanceOf(PromotionNotFound.class);
  }

  @Test
  void assigns_and_reassigns_a_teacher() {
    Promotion promotion = aPromotion();
    Teacher first = aTeacher();
    Teacher second = aTeacher();
    CourseView created = createHandler.handle(
        new CreateCourseCommand("CS101", "Algorithms", 3.0, promotion.id().toString(), first.id().toString()));

    CourseView reassigned = assignHandler.handle(
        new AssignTeacherToCourseCommand(created.id(), second.id().toString()));

    assertThat(reassigned.teacherId()).isEqualTo(second.id().toString());
  }
}
