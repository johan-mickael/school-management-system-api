package com.schoolmanagement.grading.infrastructure.persistence;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.schoolmanagement.course.domain.Course;
import com.schoolmanagement.course.domain.CourseCode;
import com.schoolmanagement.course.domain.CourseId;
import com.schoolmanagement.course.domain.CourseRepository;
import com.schoolmanagement.course.domain.CourseTitle;
import com.schoolmanagement.grading.application.command.recordgrade.RecordGradeCommand;
import com.schoolmanagement.grading.application.command.recordgrade.RecordGradeHandler;
import com.schoolmanagement.grading.application.query.getpromotionrankings.GetPromotionRankingsHandler;
import com.schoolmanagement.grading.application.query.getpromotionrankings.GetPromotionRankingsQuery;
import com.schoolmanagement.grading.application.view.PromotionRankingEntryView;
import com.schoolmanagement.grading.domain.GradeStatisticsRepository;
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
import com.schoolmanagement.student.domain.StudentNumber;
import com.schoolmanagement.student.domain.StudentRepository;

@Transactional
class GradeStatisticsIT extends AbstractIntegrationTest {

  @Autowired
  private GradeStatisticsRepository gradeStatistics;

  @Autowired
  private RecordGradeHandler recordGrade;

  @Autowired
  private GetPromotionRankingsHandler promotionRankings;

  @Autowired
  private CourseRepository courses;

  @Autowired
  private PromotionRepository promotions;

  @Autowired
  private StudentRepository students;

  private Course aCourse(PromotionId promotionId, String code) {
    Course course = Course.create(
        CourseId.generate(), new CourseCode(code), new CourseTitle("Title " + code),
        new com.schoolmanagement.course.domain.Coefficient(1.0), promotionId, null);
    courses.save(course);
    return course;
  }

  private Student aStudent(String number) {
    Student student = Student.enroll(
        com.schoolmanagement.student.domain.StudentId.generate(), new StudentNumber(number),
        new FullName("Ada", "Lovelace"), new EmailAddress(number.toLowerCase() + "@example.com"),
        Instant.parse("2025-09-01T00:00:00Z"));
    students.save(student);
    return student;
  }

  @Test
  void computes_per_course_and_overall_weighted_averages() {
    Promotion promotion = Promotion.create(
        PromotionId.generate(), new PromotionName("MSE 2025"), new AcademicYear("2025-2026"), new Capacity(30));
    promotions.save(promotion);
    Course courseA = aCourse(promotion.id(), "CS501");
    Course courseB = aCourse(promotion.id(), "CS502");
    Student student = aStudent("STU-2025-0301");

    recordGrade.handle(new RecordGradeCommand(courseA.id().toString(), student.id().toString(), null, 10.0, 1.0));
    recordGrade.handle(new RecordGradeCommand(courseA.id().toString(), student.id().toString(), null, 20.0, 3.0));
    recordGrade.handle(new RecordGradeCommand(courseB.id().toString(), student.id().toString(), null, 12.0, 1.0));

    var courseAverages = gradeStatistics.courseAveragesForStudent(student.id());
    assertThat(courseAverages).extracting(a -> a.courseId(), a -> a.average())
        .containsExactlyInAnyOrder(
            tuple(courseA.id(), 17.5),
            tuple(courseB.id(), 12.0));

    Double overall = gradeStatistics.overallAverageForStudent(student.id()).orElseThrow();
    assertThat(overall).isEqualTo((10.0 * 1 + 20.0 * 3 + 12.0 * 1) / (1 + 3 + 1));
  }

  @Test
  void a_student_with_no_grades_has_no_average() {
    Student student = aStudent("STU-2025-0302");

    assertThat(gradeStatistics.courseAveragesForStudent(student.id())).isEmpty();
    assertThat(gradeStatistics.overallAverageForStudent(student.id())).isEmpty();
  }

  @Test
  void ranks_a_promotions_students_by_descending_average() {
    Promotion promotion = Promotion.create(
        PromotionId.generate(), new PromotionName("MSE 2025"), new AcademicYear("2025-2026"), new Capacity(30));
    promotions.save(promotion);
    Course course = aCourse(promotion.id(), "CS601");

    Student top = aStudent("STU-2025-0401");
    top.assignToPromotion(promotion.id());
    students.save(top);
    recordGrade.handle(new RecordGradeCommand(course.id().toString(), top.id().toString(), null, 18.0, 1.0));

    Student middle = aStudent("STU-2025-0402");
    middle.assignToPromotion(promotion.id());
    students.save(middle);
    recordGrade.handle(new RecordGradeCommand(course.id().toString(), middle.id().toString(), null, 10.0, 1.0));

    Student ungraded = aStudent("STU-2025-0403");
    ungraded.assignToPromotion(promotion.id());
    students.save(ungraded);

    java.util.List<PromotionRankingEntryView> ranking =
        promotionRankings.handle(new GetPromotionRankingsQuery(promotion.id().toString()));

    assertThat(ranking).extracting(PromotionRankingEntryView::studentId, PromotionRankingEntryView::rank)
        .containsExactly(
            tuple(top.id().toString(), 1),
            tuple(middle.id().toString(), 2),
            tuple(ungraded.id().toString(), 3));
  }
}
