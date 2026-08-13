package com.schoolmanagement.grading.infrastructure.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.schoolmanagement.course.domain.CourseId;
import com.schoolmanagement.grading.domain.CourseAverage;
import com.schoolmanagement.grading.domain.GradeStatisticsRepository;
import com.schoolmanagement.student.domain.StudentId;

@Repository
public class GradeStatisticsRepositoryAdapter implements GradeStatisticsRepository {
  private final GradeJpaRepository gradeRepository;

  public GradeStatisticsRepositoryAdapter(GradeJpaRepository gradeRepository) {
    this.gradeRepository = gradeRepository;
  }

  @Override
  public List<CourseAverage> courseAveragesForStudent(StudentId studentId) {
    return gradeRepository.courseAveragesByStudentId(studentId.value()).stream()
        .map(row -> new CourseAverage(new CourseId(row.courseId()), row.average()))
        .toList();
  }

  @Override
  public Optional<Double> overallAverageForStudent(StudentId studentId) {
    return Optional.ofNullable(gradeRepository.overallAverageByStudentId(studentId.value()));
  }
}
