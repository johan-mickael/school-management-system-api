package com.schoolmanagement.grading.application.command.correctgrade;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.schoolmanagement.grading.application.view.GradeView;
import com.schoolmanagement.grading.domain.Grade;
import com.schoolmanagement.grading.domain.GradeId;
import com.schoolmanagement.grading.domain.GradeRepository;
import com.schoolmanagement.grading.domain.Score;

@Service
public class CorrectGradeHandler {
  private final GradeRepository grades;

  public CorrectGradeHandler(GradeRepository grades) {
    this.grades = grades;
  }

  @Transactional
  public GradeView handle(CorrectGradeCommand command) {
    Grade grade = grades.getById(GradeId.of(command.gradeId()));

    grade.correct(new Score(command.newScore()));
    grades.save(grade);

    return GradeView.from(grade);
  }
}
