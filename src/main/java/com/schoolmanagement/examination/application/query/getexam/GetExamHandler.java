package com.schoolmanagement.examination.application.query.getexam;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.schoolmanagement.examination.application.view.ExamView;
import com.schoolmanagement.examination.domain.Exam;
import com.schoolmanagement.examination.domain.ExamId;
import com.schoolmanagement.examination.domain.ExamRepository;

@Service
public class GetExamHandler {
  private final ExamRepository exams;

  public GetExamHandler(ExamRepository exams) {
    this.exams = exams;
  }

  @Transactional(readOnly = true)
  public ExamView handle(GetExamQuery query) {
    Exam foundExam = exams.getById(ExamId.of(query.examId()));

    return ExamView.from(foundExam);
  }
}
