package com.schoolmanagement.reporting.application.query.getatriskstudents;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.schoolmanagement.attendance.domain.AttendanceStatisticsRepository;
import com.schoolmanagement.grading.domain.GradeStatisticsRepository;
import com.schoolmanagement.promotion.domain.PromotionId;
import com.schoolmanagement.promotion.domain.PromotionRepository;
import com.schoolmanagement.reporting.application.view.AtRiskStudentView;
import com.schoolmanagement.student.domain.Student;
import com.schoolmanagement.student.domain.StudentRepository;

@Service
public class GetAtRiskStudentsHandler {
  private final PromotionRepository promotions;
  private final StudentRepository students;
  private final AttendanceStatisticsRepository attendanceStatistics;
  private final GradeStatisticsRepository gradeStatistics;
  private final double attendanceRiskThreshold;
  private final double gradeRiskThreshold;

  public GetAtRiskStudentsHandler(
      PromotionRepository promotions,
      StudentRepository students,
      AttendanceStatisticsRepository attendanceStatistics,
      GradeStatisticsRepository gradeStatistics,
      @Value("${app.reporting.attendance-risk-threshold}") double attendanceRiskThreshold,
      @Value("${app.reporting.grade-risk-threshold}") double gradeRiskThreshold) {
    this.promotions = promotions;
    this.students = students;
    this.attendanceStatistics = attendanceStatistics;
    this.gradeStatistics = gradeStatistics;
    this.attendanceRiskThreshold = attendanceRiskThreshold;
    this.gradeRiskThreshold = gradeRiskThreshold;
  }

  @Transactional(readOnly = true)
  public List<AtRiskStudentView> handle(GetAtRiskStudentsQuery query) {
    PromotionId promotionId = PromotionId.of(query.promotionId());
    promotions.getById(promotionId);

    return students.findByPromotionId(promotionId).stream()
        .map(this::toView)
        .filter(this::isAtRisk)
        .toList();
  }

  private AtRiskStudentView toView(Student student) {
    Double attendanceRate = attendanceStatistics.attendanceRateForStudent(student.id()).orElse(null);
    Double averageGrade = gradeStatistics.overallAverageForStudent(student.id()).orElse(null);
    return new AtRiskStudentView(student.id().toString(), attendanceRate, averageGrade);
  }

  private boolean isAtRisk(AtRiskStudentView view) {
    boolean lowAttendance = view.attendanceRate() != null && view.attendanceRate() < attendanceRiskThreshold;
    boolean lowGrade = view.averageGrade() != null && view.averageGrade() < gradeRiskThreshold;
    return lowAttendance || lowGrade;
  }
}
