package com.schoolmanagement.grading.application.command.recordgrade;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.schoolmanagement.course.domain.CourseId;
import com.schoolmanagement.course.domain.CourseRepository;
import com.schoolmanagement.grading.application.view.GradeView;
import com.schoolmanagement.grading.domain.Coefficient;
import com.schoolmanagement.grading.domain.Grade;
import com.schoolmanagement.grading.domain.GradeId;
import com.schoolmanagement.grading.domain.GradeRepository;
import com.schoolmanagement.grading.domain.Score;
import com.schoolmanagement.student.domain.StudentId;
import com.schoolmanagement.student.domain.StudentRepository;

@Service
public class RecordGradeHandler {
  private final GradeRepository grades;
  private final CourseRepository courses;
  private final StudentRepository students;

  public RecordGradeHandler(GradeRepository grades, CourseRepository courses, StudentRepository students) {
    this.grades = grades;
    this.courses = courses;
    this.students = students;
  }

  @Transactional
  public GradeView handle(RecordGradeCommand command) {
    CourseId courseId = CourseId.of(command.courseId());
    courses.getById(courseId);
    StudentId studentId = StudentId.of(command.studentId());
    students.getById(studentId);

    Grade grade = Grade.record(
        GradeId.generate(),
        studentId,
        courseId,
        command.examId() == null || command.examId().isBlank() ? null : UUID.fromString(command.examId()),
        new Score(command.score()),
        new Coefficient(command.coefficient()));

    grades.save(grade);

    return GradeView.from(grade);
  }
}
