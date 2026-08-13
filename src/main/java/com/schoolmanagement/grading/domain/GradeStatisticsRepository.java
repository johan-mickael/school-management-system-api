package com.schoolmanagement.grading.domain;

import java.util.List;
import java.util.Optional;

import com.schoolmanagement.student.domain.StudentId;

public interface GradeStatisticsRepository {
    List<CourseAverage> courseAveragesForStudent(StudentId studentId);

    Optional<Double> overallAverageForStudent(StudentId studentId);
}
