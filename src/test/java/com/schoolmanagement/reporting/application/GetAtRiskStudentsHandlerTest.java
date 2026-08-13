package com.schoolmanagement.reporting.application;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.schoolmanagement.attendance.domain.AttendanceStatisticsRepository;
import com.schoolmanagement.grading.domain.GradeStatisticsRepository;
import com.schoolmanagement.grading.domain.CourseAverage;
import com.schoolmanagement.promotion.domain.AcademicYear;
import com.schoolmanagement.promotion.domain.Capacity;
import com.schoolmanagement.promotion.domain.Promotion;
import com.schoolmanagement.promotion.domain.PromotionId;
import com.schoolmanagement.promotion.domain.PromotionName;
import com.schoolmanagement.promotion.domain.PromotionRepository;
import com.schoolmanagement.promotion.domain.exception.PromotionNotFound;
import com.schoolmanagement.reporting.application.query.getatriskstudents.GetAtRiskStudentsHandler;
import com.schoolmanagement.reporting.application.query.getatriskstudents.GetAtRiskStudentsQuery;
import com.schoolmanagement.reporting.application.view.AtRiskStudentView;
import com.schoolmanagement.shared.domain.EmailAddress;
import com.schoolmanagement.shared.domain.FullName;
import com.schoolmanagement.student.domain.Student;
import com.schoolmanagement.student.domain.StudentId;
import com.schoolmanagement.student.domain.StudentNumber;
import com.schoolmanagement.student.domain.StudentRepository;
import com.schoolmanagement.student.domain.exception.StudentNotFound;

class GetAtRiskStudentsHandlerTest {

  static class InMemoryPromotions implements PromotionRepository {
    final Map<UUID, Promotion> db = new HashMap<>();

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

  static class InMemoryStudents implements StudentRepository {
    final Map<UUID, Student> db = new HashMap<>();

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
      return db.values().stream()
          .filter(s -> promotionId.equals(s.promotionId()))
          .toList();
    }
  }

  static class FakeAttendanceStatistics implements AttendanceStatisticsRepository {
    final Map<StudentId, Double> rates = new HashMap<>();

    public Optional<Double> attendanceRateForStudent(StudentId studentId) {
      return Optional.ofNullable(rates.get(studentId));
    }
  }

  static class FakeGradeStatistics implements GradeStatisticsRepository {
    final Map<StudentId, Double> averages = new HashMap<>();

    public List<CourseAverage> courseAveragesForStudent(StudentId studentId) {
      return List.of();
    }

    public Optional<Double> overallAverageForStudent(StudentId studentId) {
      return Optional.ofNullable(averages.get(studentId));
    }
  }

  private final InMemoryPromotions promotions = new InMemoryPromotions();
  private final InMemoryStudents students = new InMemoryStudents();
  private final FakeAttendanceStatistics attendanceStatistics = new FakeAttendanceStatistics();
  private final FakeGradeStatistics gradeStatistics = new FakeGradeStatistics();
  private final GetAtRiskStudentsHandler handler = new GetAtRiskStudentsHandler(
      promotions, students, attendanceStatistics, gradeStatistics, 0.75, 10.0);

  private Student aStudent(String number, Promotion promotion) {
    Student student = Student.enroll(
        StudentId.generate(), new StudentNumber(number),
        new FullName("Ada", "Lovelace"), new EmailAddress(number.toLowerCase() + "@example.com"),
        Instant.parse("2025-09-01T00:00:00Z"));
    student.assignToPromotion(promotion.id());
    students.save(student);
    return student;
  }

  @Test
  void flags_a_student_below_the_attendance_threshold() {
    Promotion promotion = Promotion.create(
        PromotionId.generate(), new PromotionName("MSE 2025"), new AcademicYear("2025-2026"), new Capacity(30));
    promotions.save(promotion);
    Student student = aStudent("STU-2025-0001", promotion);
    attendanceStatistics.rates.put(student.id(), 0.5);
    gradeStatistics.averages.put(student.id(), 15.0);

    List<AtRiskStudentView> atRisk = handler.handle(new GetAtRiskStudentsQuery(promotion.id().toString()));

    assertThat(atRisk).extracting(AtRiskStudentView::studentId).containsExactly(student.id().toString());
  }

  @Test
  void flags_a_student_below_the_grade_threshold() {
    Promotion promotion = Promotion.create(
        PromotionId.generate(), new PromotionName("MSE 2025"), new AcademicYear("2025-2026"), new Capacity(30));
    promotions.save(promotion);
    Student student = aStudent("STU-2025-0002", promotion);
    attendanceStatistics.rates.put(student.id(), 0.9);
    gradeStatistics.averages.put(student.id(), 8.0);

    List<AtRiskStudentView> atRisk = handler.handle(new GetAtRiskStudentsQuery(promotion.id().toString()));

    assertThat(atRisk).extracting(AtRiskStudentView::studentId).containsExactly(student.id().toString());
  }

  @Test
  void does_not_flag_a_student_with_no_data_at_all() {
    Promotion promotion = Promotion.create(
        PromotionId.generate(), new PromotionName("MSE 2025"), new AcademicYear("2025-2026"), new Capacity(30));
    promotions.save(promotion);
    aStudent("STU-2025-0003", promotion);

    List<AtRiskStudentView> atRisk = handler.handle(new GetAtRiskStudentsQuery(promotion.id().toString()));

    assertThat(atRisk).isEmpty();
  }

  @Test
  void does_not_flag_a_student_above_both_thresholds() {
    Promotion promotion = Promotion.create(
        PromotionId.generate(), new PromotionName("MSE 2025"), new AcademicYear("2025-2026"), new Capacity(30));
    promotions.save(promotion);
    Student student = aStudent("STU-2025-0004", promotion);
    attendanceStatistics.rates.put(student.id(), 0.95);
    gradeStatistics.averages.put(student.id(), 16.0);

    List<AtRiskStudentView> atRisk = handler.handle(new GetAtRiskStudentsQuery(promotion.id().toString()));

    assertThat(atRisk).isEmpty();
  }
}
