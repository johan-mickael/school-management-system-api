package com.schoolmanagement.grading.infrastructure.persistence;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.schoolmanagement.course.domain.Course;
import com.schoolmanagement.course.domain.CourseCode;
import com.schoolmanagement.course.domain.CourseId;
import com.schoolmanagement.course.domain.CourseRepository;
import com.schoolmanagement.course.domain.CourseTitle;
import com.schoolmanagement.grading.domain.Grade;
import com.schoolmanagement.grading.domain.GradeId;
import com.schoolmanagement.grading.domain.GradeRepository;
import com.schoolmanagement.grading.domain.Score;
import com.schoolmanagement.grading.domain.exception.GradeNotFound;
import com.schoolmanagement.promotion.domain.AcademicYear;
import com.schoolmanagement.promotion.domain.Capacity;
import com.schoolmanagement.promotion.domain.Promotion;
import com.schoolmanagement.promotion.domain.PromotionId;
import com.schoolmanagement.promotion.domain.PromotionName;
import com.schoolmanagement.promotion.domain.PromotionRepository;
import com.schoolmanagement.shared.AbstractIntegrationTest;
import com.schoolmanagement.shared.domain.EmailAddress;
import com.schoolmanagement.shared.domain.FullName;
import com.schoolmanagement.student.domain.Student;
import com.schoolmanagement.student.domain.StudentId;
import com.schoolmanagement.student.domain.StudentNumber;
import com.schoolmanagement.student.domain.StudentRepository;

@Transactional
class GradeRepositoryAdapterIT extends AbstractIntegrationTest {

  @Autowired
  private GradeRepository grades;

  @Autowired
  private CourseRepository courses;

  @Autowired
  private PromotionRepository promotions;

  @Autowired
  private StudentRepository students;

  @Test
  void saves_and_retrieves_a_grade() {
    Promotion promotion = Promotion.create(
        PromotionId.generate(), new PromotionName("MSE 2025"), new AcademicYear("2025-2026"), new Capacity(30));
    promotions.save(promotion);
    Course course = Course.create(
        CourseId.generate(), new CourseCode("CS401"), new CourseTitle("Compilers"),
        new com.schoolmanagement.course.domain.Coefficient(2.0), promotion.id(), null);
    courses.save(course);
    Student student = Student.enroll(
        StudentId.generate(), new StudentNumber("STU-2025-0055"), new FullName("Ada", "Lovelace"),
        new EmailAddress("ada.grade@example.com"), Instant.parse("2025-09-01T00:00:00Z"));
    students.save(student);

    Grade grade = Grade.record(
        GradeId.generate(), student.id(), course.id(), null, new Score(16.5), new com.schoolmanagement.grading.domain.Coefficient(2.0));

    grades.save(grade);

    Grade found = grades.getById(grade.id());
    assertThat(found.score()).isEqualTo(grade.score());
    assertThat(found.studentId()).isEqualTo(student.id());
    assertThat(found.courseId()).isEqualTo(course.id());
    assertThat(found.examId()).isNull();
  }

  @Test
  void throws_when_grade_not_found() {
    assertThatThrownBy(() -> grades.getById(GradeId.generate()))
        .isInstanceOf(GradeNotFound.class);
  }
}
