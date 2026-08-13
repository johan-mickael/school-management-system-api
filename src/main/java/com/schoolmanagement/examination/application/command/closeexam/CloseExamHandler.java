package com.schoolmanagement.examination.application.command.closeexam;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.schoolmanagement.examination.application.view.ExamView;
import com.schoolmanagement.examination.domain.Exam;
import com.schoolmanagement.examination.domain.ExamId;
import com.schoolmanagement.examination.domain.ExamRepository;

@Service
public class CloseExamHandler {
  private final ExamRepository exams;

  public CloseExamHandler(ExamRepository exams) {
    this.exams = exams;
  }

  @Transactional
  public ExamView handle(CloseExamCommand command) {
    Exam exam = exams.getById(ExamId.of(command.examId()));

    exam.close();
    exams.save(exam);

    return ExamView.from(exam);
  }
}
