package com.schoolmanagement.examination.infrastructure.persistence;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.transaction.annotation.Transactional;

import com.schoolmanagement.course.domain.Coefficient;
import com.schoolmanagement.course.domain.Course;
import com.schoolmanagement.course.domain.CourseCode;
import com.schoolmanagement.course.domain.CourseId;
import com.schoolmanagement.course.domain.CourseRepository;
import com.schoolmanagement.course.domain.CourseTitle;
import com.schoolmanagement.examination.domain.AttemptId;
import com.schoolmanagement.examination.domain.Exam;
import com.schoolmanagement.examination.domain.ExamAttempt;
import com.schoolmanagement.examination.domain.ExamAttemptRepository;
import com.schoolmanagement.examination.domain.ExamId;
import com.schoolmanagement.examination.domain.ExamRepository;
import com.schoolmanagement.examination.domain.IntegrityEvent;
import com.schoolmanagement.examination.domain.IntegrityEventType;
import com.schoolmanagement.examination.domain.exception.AttemptNotFound;
import com.schoolmanagement.promotion.domain.AcademicYear;
import com.schoolmanagement.promotion.domain.Capacity;
import com.schoolmanagement.promotion.domain.Promotion;
import com.schoolmanagement.promotion.domain.PromotionId;
import com.schoolmanagement.promotion.domain.PromotionName;
import com.schoolmanagement.promotion.domain.PromotionRepository;
import com.schoolmanagement.shared.AbstractIntegrationTest;
import com.schoolmanagement.shared.domain.TimeWindow;
import com.schoolmanagement.student.domain.Student;
import com.schoolmanagement.student.domain.StudentNumber;
import com.schoolmanagement.student.domain.StudentRepository;
import com.schoolmanagement.shared.domain.EmailAddress;
import com.schoolmanagement.shared.domain.FullName;

class ExamAttemptRepositoryAdapterIT extends AbstractIntegrationTest {

  @Autowired
  private ExamAttemptRepository attempts;

  @Autowired
  private ExamRepository exams;

  @Autowired
  private CourseRepository courses;

  @Autowired
  private PromotionRepository promotions;

  @Autowired
  private StudentRepository students;

  private Exam anExam() {
    Promotion promotion = Promotion.create(
        PromotionId.generate(), new PromotionName("MSE 2025"), new AcademicYear("2025-2026"), new Capacity(30));
    promotions.save(promotion);
    Course course = Course.create(
        CourseId.generate(), new CourseCode("CS801"), new CourseTitle("Cryptography"),
        new Coefficient(2.0), promotion.id(), null);
    courses.save(course);
    Exam exam = Exam.schedule(
        ExamId.generate(), course.id(), promotion.id(),
        new TimeWindow(Instant.parse("2025-12-01T08:00:00Z"), Instant.parse("2025-12-01T10:00:00Z")));
    exams.save(exam);
    return exam;
  }

  private Student aStudent() {
    Student student = Student.enroll(
        com.schoolmanagement.student.domain.StudentId.generate(), new StudentNumber("STU-2025-0501"),
        new FullName("Ada", "Lovelace"), new EmailAddress("ada.attempt@example.com"),
        Instant.parse("2025-09-01T00:00:00Z"));
    students.save(student);
    return student;
  }

  @Test
  @Transactional
  void saves_and_retrieves_an_attempt_with_its_events() {
    Exam exam = anExam();
    Student student = aStudent();
    ExamAttempt attempt = ExamAttempt.start(AttemptId.generate(), exam.id(), student.id());
    attempt.recordEvent(new IntegrityEvent(IntegrityEventType.TAB_SWITCH, Instant.parse("2025-12-01T08:05:00Z")));
    attempt.recordEvent(new IntegrityEvent(IntegrityEventType.COPY, Instant.parse("2025-12-01T08:06:00Z")));

    attempts.save(attempt);

    ExamAttempt found = attempts.getById(attempt.id());
    assertThat(found.events()).hasSize(2);
    assertThat(found.integrityScore().value()).isEqualTo(2);
  }

  @Test
  @Transactional
  void throws_when_attempt_not_found() {
    assertThatThrownBy(() -> attempts.getById(AttemptId.generate()))
        .isInstanceOf(AttemptNotFound.class);
  }

  @Test
  void detects_concurrent_submissions_via_optimistic_locking() {
    Exam exam = anExam();
    Student student = aStudent();
    ExamAttempt attempt = ExamAttempt.start(AttemptId.generate(), exam.id(), student.id());
    attempts.save(attempt);

    ExamAttempt staleCopy = attempts.getById(attempt.id());
    ExamAttempt freshCopy = attempts.getById(attempt.id());

    freshCopy.submit();
    attempts.save(freshCopy);

    staleCopy.flag();
    assertThatThrownBy(() -> attempts.save(staleCopy))
        .isInstanceOf(ObjectOptimisticLockingFailureException.class);
  }
}
