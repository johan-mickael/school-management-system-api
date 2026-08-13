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
import com.schoolmanagement.examination.domain.Exam;
import com.schoolmanagement.examination.domain.ExamId;
import com.schoolmanagement.examination.domain.ExamRepository;
import com.schoolmanagement.examination.domain.exception.ExamNotFound;
import com.schoolmanagement.promotion.domain.AcademicYear;
import com.schoolmanagement.promotion.domain.Capacity;
import com.schoolmanagement.promotion.domain.Promotion;
import com.schoolmanagement.promotion.domain.PromotionId;
import com.schoolmanagement.promotion.domain.PromotionName;
import com.schoolmanagement.promotion.domain.PromotionRepository;
import com.schoolmanagement.shared.AbstractIntegrationTest;
import com.schoolmanagement.shared.domain.TimeWindow;

class ExamRepositoryAdapterIT extends AbstractIntegrationTest {

  @Autowired
  private ExamRepository exams;

  @Autowired
  private CourseRepository courses;

  @Autowired
  private PromotionRepository promotions;

  private Exam aScheduledExam() {
    Promotion promotion = Promotion.create(
        PromotionId.generate(), new PromotionName("MSE 2025"), new AcademicYear("2025-2026"), new Capacity(30));
    promotions.save(promotion);
    Course course = Course.create(
        CourseId.generate(), new CourseCode("CS701"), new CourseTitle("Distributed Systems"),
        new Coefficient(2.0), promotion.id(), null);
    courses.save(course);

    return Exam.schedule(
        ExamId.generate(), course.id(), promotion.id(),
        new TimeWindow(Instant.parse("2025-12-01T08:00:00Z"), Instant.parse("2025-12-01T10:00:00Z")));
  }

  @Test
  @Transactional
  void saves_and_retrieves_an_exam() {
    Exam exam = aScheduledExam();

    exams.save(exam);

    Exam found = exams.getById(exam.id());
    assertThat(found.id()).isEqualTo(exam.id());
    assertThat(found.status()).isEqualTo(exam.status());
    assertThat(found.timeWindow()).isEqualTo(exam.timeWindow());
  }

  @Test
  @Transactional
  void throws_when_exam_not_found() {
    assertThatThrownBy(() -> exams.getById(ExamId.generate()))
        .isInstanceOf(ExamNotFound.class);
  }

  @Test
  void detects_concurrent_transitions_via_optimistic_locking() {
    Exam exam = aScheduledExam();
    exams.save(exam);

    Exam staleCopy = exams.getById(exam.id());
    Exam freshCopy = exams.getById(exam.id());

    freshCopy.open();
    exams.save(freshCopy);

    staleCopy.open();
    assertThatThrownBy(() -> exams.save(staleCopy))
        .isInstanceOf(ObjectOptimisticLockingFailureException.class);
  }
}
