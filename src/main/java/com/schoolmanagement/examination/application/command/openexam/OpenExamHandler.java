package com.schoolmanagement.examination.application.command.openexam;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.schoolmanagement.examination.application.view.ExamView;
import com.schoolmanagement.examination.domain.Exam;
import com.schoolmanagement.examination.domain.ExamId;
import com.schoolmanagement.examination.domain.ExamRepository;

@Service
public class OpenExamHandler {
  private final ExamRepository exams;

  public OpenExamHandler(ExamRepository exams) {
    this.exams = exams;
  }

  @Transactional
  public ExamView handle(OpenExamCommand command) {
    Exam exam = exams.getById(ExamId.of(command.examId()));

    exam.open();
    exams.save(exam);

    return ExamView.from(exam);
  }
}
