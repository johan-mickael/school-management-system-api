package com.schoolmanagement.reporting.application.query.getstudentsummary;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.schoolmanagement.attendance.domain.AttendanceStatisticsRepository;
import com.schoolmanagement.grading.domain.GradeStatisticsRepository;
import com.schoolmanagement.reporting.application.view.StudentSummaryView;
import com.schoolmanagement.student.domain.StudentId;
import com.schoolmanagement.student.domain.StudentRepository;

@Service
public class GetStudentSummaryHandler {
  private final StudentRepository students;
  private final AttendanceStatisticsRepository attendanceStatistics;
  private final GradeStatisticsRepository gradeStatistics;

  public GetStudentSummaryHandler(
      StudentRepository students,
      AttendanceStatisticsRepository attendanceStatistics,
      GradeStatisticsRepository gradeStatistics) {
    this.students = students;
    this.attendanceStatistics = attendanceStatistics;
    this.gradeStatistics = gradeStatistics;
  }

  @Transactional(readOnly = true)
  public StudentSummaryView handle(GetStudentSummaryQuery query) {
    StudentId studentId = StudentId.of(query.studentId());
    students.getById(studentId);

    Double attendanceRate = attendanceStatistics.attendanceRateForStudent(studentId).orElse(null);
    Double averageGrade = gradeStatistics.overallAverageForStudent(studentId).orElse(null);

    return new StudentSummaryView(studentId.toString(), attendanceRate, averageGrade);
  }
}
