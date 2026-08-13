package com.schoolmanagement.grading.infrastructure.persistence;

import org.springframework.stereotype.Component;

import com.schoolmanagement.course.domain.CourseId;
import com.schoolmanagement.grading.domain.Coefficient;
import com.schoolmanagement.grading.domain.Grade;
import com.schoolmanagement.grading.domain.GradeId;
import com.schoolmanagement.grading.domain.Score;
import com.schoolmanagement.student.domain.StudentId;

@Component
public class GradeMapper {

  public GradeEntity toEntity(Grade g) {
    return new GradeEntity(
        g.id().value(),
        g.studentId().value(),
        g.courseId().value(),
        g.examId(),
        g.score().value(),
        g.coefficient().value());
  }

  public Grade toDomain(GradeEntity e) {
    return Grade.reconstitute(
        new GradeId(e.getId()),
        new StudentId(e.getStudentId()),
        new CourseId(e.getCourseId()),
        e.getExamId(),
        new Score(e.getScore()),
        new Coefficient(e.getCoefficient()));
  }
}
