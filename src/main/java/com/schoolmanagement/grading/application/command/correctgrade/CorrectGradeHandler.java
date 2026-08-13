package com.schoolmanagement.grading.application.command.correctgrade;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.schoolmanagement.grading.application.view.GradeView;
import com.schoolmanagement.grading.domain.Grade;
import com.schoolmanagement.grading.domain.GradeId;
import com.schoolmanagement.grading.domain.GradeRepository;
import com.schoolmanagement.grading.domain.Score;
import com.schoolmanagement.student.domain.Student;
import com.schoolmanagement.student.domain.StudentRepository;

@Service
public class CorrectGradeHandler {
  private final GradeRepository grades;
  private final StudentRepository students;

  public CorrectGradeHandler(GradeRepository grades, StudentRepository students) {
    this.grades = grades;
    this.students = students;
  }

  @Transactional
  public GradeView handle(CorrectGradeCommand command) {
    Grade grade = grades.getById(GradeId.of(command.gradeId()));

    Student student = students.getById(grade.studentId());
    student.ensureActive();

    grade.correct(new Score(command.newScore()));
    grades.save(grade);

    return GradeView.from(grade);
  }
}
