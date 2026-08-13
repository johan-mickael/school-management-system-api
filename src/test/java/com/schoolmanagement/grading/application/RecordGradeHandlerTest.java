package com.schoolmanagement.grading.application;

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
import com.schoolmanagement.grading.application.command.correctgrade.CorrectGradeCommand;
import com.schoolmanagement.grading.application.command.correctgrade.CorrectGradeHandler;
import com.schoolmanagement.grading.application.command.recordgrade.RecordGradeCommand;
import com.schoolmanagement.grading.application.command.recordgrade.RecordGradeHandler;
import com.schoolmanagement.grading.application.view.GradeView;
import com.schoolmanagement.grading.domain.Grade;
import com.schoolmanagement.grading.domain.GradeId;
import com.schoolmanagement.grading.domain.GradeRepository;
import com.schoolmanagement.grading.domain.exception.GradeNotFound;
import com.schoolmanagement.grading.domain.exception.InvalidScore;
import com.schoolmanagement.promotion.domain.PromotionId;
import com.schoolmanagement.shared.domain.EmailAddress;
import com.schoolmanagement.shared.domain.FullName;
import com.schoolmanagement.student.domain.Student;
import com.schoolmanagement.student.domain.StudentId;
import com.schoolmanagement.student.domain.StudentNumber;
import com.schoolmanagement.student.domain.StudentRepository;
import com.schoolmanagement.student.domain.exception.StudentNotFound;

class RecordGradeHandlerTest {
  static class InMemoryGrades implements GradeRepository {
    final List<Grade> db = new ArrayList<>();

    public void save(Grade g) {
      db.removeIf(existing -> existing.id().equals(g.id()));
      db.add(g);
    }

    public Grade getById(GradeId id) {
      return db.stream()
          .filter(g -> g.id().equals(id))
          .findFirst()
          .orElseThrow(() -> new GradeNotFound(id));
    }

    public List<Grade> findByStudentId(StudentId studentId) {
      return db.stream().filter(g -> g.studentId().equals(studentId)).toList();
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

  private final InMemoryGrades grades = new InMemoryGrades();
  private final InMemoryCourses courses = new InMemoryCourses();
  private final InMemoryStudents students = new InMemoryStudents();
  private final RecordGradeHandler recordHandler = new RecordGradeHandler(grades, courses, students);
  private final CorrectGradeHandler correctHandler = new CorrectGradeHandler(grades);

  private Course aCourse() {
    Course course = Course.create(
        CourseId.generate(), new CourseCode("CS101"), new CourseTitle("Algorithms"),
        new Coefficient(3.0), PromotionId.generate(), null);
    courses.save(course);
    return course;
  }

  private Student aStudent() {
    Student student = Student.enroll(
        StudentId.generate(), new StudentNumber("STU-2025-0001"), new FullName("Ada", "Lovelace"),
        new EmailAddress("ada@example.com"), Instant.parse("2025-09-01T00:00:00Z"));
    students.save(student);
    return student;
  }

  @Test
  void rejects_recording_a_grade_for_an_archived_student() {
    Course course = aCourse();
    Student student = aStudent();
    student.archive();
    students.save(student);

    assertThatThrownBy(() -> recordHandler.handle(
        new RecordGradeCommand(course.id().toString(), student.id().toString(), null, 15.5, 2.0)))
            .isInstanceOf(com.schoolmanagement.student.domain.exception.StudentAlreadyArchived.class);
  }

  @Test
  void records_a_grade() {
    Course course = aCourse();
    Student student = aStudent();

    GradeView view = recordHandler.handle(
        new RecordGradeCommand(course.id().toString(), student.id().toString(), null, 15.5, 2.0));

    assertThat(view.score()).isEqualTo(15.5);
  }

  @Test
  void rejects_a_score_above_the_scale() {
    Course course = aCourse();
    Student student = aStudent();

    assertThatThrownBy(() -> recordHandler.handle(
        new RecordGradeCommand(course.id().toString(), student.id().toString(), null, 20.5, 2.0)))
            .isInstanceOf(InvalidScore.class);
  }

  @Test
  void rejects_a_negative_score() {
    Course course = aCourse();
    Student student = aStudent();

    assertThatThrownBy(() -> recordHandler.handle(
        new RecordGradeCommand(course.id().toString(), student.id().toString(), null, -0.5, 2.0)))
            .isInstanceOf(InvalidScore.class);
  }

  @Test
  void accepts_the_scale_boundaries() {
    Course course = aCourse();
    Student student = aStudent();

    GradeView zero = recordHandler.handle(
        new RecordGradeCommand(course.id().toString(), student.id().toString(), null, 0.0, 1.0));
    GradeView twenty = recordHandler.handle(
        new RecordGradeCommand(course.id().toString(), student.id().toString(), null, 20.0, 1.0));

    assertThat(zero.score()).isEqualTo(0.0);
    assertThat(twenty.score()).isEqualTo(20.0);
  }

  @Test
  void corrects_a_recorded_grade() {
    Course course = aCourse();
    Student student = aStudent();
    GradeView recorded = recordHandler.handle(
        new RecordGradeCommand(course.id().toString(), student.id().toString(), null, 10.0, 2.0));

    GradeView corrected = correctHandler.handle(new CorrectGradeCommand(recorded.id(), 14.0));

    assertThat(corrected.score()).isEqualTo(14.0);
  }
}
