package com.schoolmanagement.grading.application.query.getstudentaverages;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.schoolmanagement.grading.application.view.CourseAverageView;
import com.schoolmanagement.grading.application.view.StudentAveragesView;
import com.schoolmanagement.grading.domain.GradeStatisticsRepository;
import com.schoolmanagement.student.domain.StudentId;
import com.schoolmanagement.student.domain.StudentRepository;

@Service
public class GetStudentAveragesHandler {
  private final GradeStatisticsRepository gradeStatistics;
  private final StudentRepository students;

  public GetStudentAveragesHandler(GradeStatisticsRepository gradeStatistics, StudentRepository students) {
    this.gradeStatistics = gradeStatistics;
    this.students = students;
  }

  @Transactional(readOnly = true)
  public StudentAveragesView handle(GetStudentAveragesQuery query) {
    StudentId studentId = StudentId.of(query.studentId());
    students.getById(studentId);

    var courseAverages = gradeStatistics.courseAveragesForStudent(studentId).stream()
        .map(CourseAverageView::from)
        .toList();
    Double overallAverage = gradeStatistics.overallAverageForStudent(studentId).orElse(null);

    return new StudentAveragesView(studentId.toString(), courseAverages, overallAverage);
  }
}
