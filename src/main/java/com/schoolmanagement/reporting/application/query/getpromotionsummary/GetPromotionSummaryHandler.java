package com.schoolmanagement.reporting.application.query.getpromotionsummary;

import java.util.List;
import java.util.OptionalDouble;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.schoolmanagement.attendance.domain.AttendanceStatisticsRepository;
import com.schoolmanagement.grading.domain.GradeStatisticsRepository;
import com.schoolmanagement.promotion.domain.PromotionId;
import com.schoolmanagement.promotion.domain.PromotionRepository;
import com.schoolmanagement.reporting.application.view.PromotionSummaryView;
import com.schoolmanagement.student.domain.Student;
import com.schoolmanagement.student.domain.StudentRepository;

@Service
public class GetPromotionSummaryHandler {
  private final PromotionRepository promotions;
  private final StudentRepository students;
  private final AttendanceStatisticsRepository attendanceStatistics;
  private final GradeStatisticsRepository gradeStatistics;

  public GetPromotionSummaryHandler(
      PromotionRepository promotions,
      StudentRepository students,
      AttendanceStatisticsRepository attendanceStatistics,
      GradeStatisticsRepository gradeStatistics) {
    this.promotions = promotions;
    this.students = students;
    this.attendanceStatistics = attendanceStatistics;
    this.gradeStatistics = gradeStatistics;
  }

  @Transactional(readOnly = true)
  public PromotionSummaryView handle(GetPromotionSummaryQuery query) {
    PromotionId promotionId = PromotionId.of(query.promotionId());
    promotions.getById(promotionId);

    List<Student> promotionStudents = students.findByPromotionId(promotionId);

    OptionalDouble averageAttendanceRate = promotionStudents.stream()
        .flatMap(s -> attendanceStatistics.attendanceRateForStudent(s.id()).stream())
        .mapToDouble(Double::doubleValue)
        .average();
    OptionalDouble averageGrade = promotionStudents.stream()
        .flatMap(s -> gradeStatistics.overallAverageForStudent(s.id()).stream())
        .mapToDouble(Double::doubleValue)
        .average();

    return new PromotionSummaryView(
        promotionId.toString(),
        promotionStudents.size(),
        averageAttendanceRate.isPresent() ? averageAttendanceRate.getAsDouble() : null,
        averageGrade.isPresent() ? averageGrade.getAsDouble() : null);
  }
}
