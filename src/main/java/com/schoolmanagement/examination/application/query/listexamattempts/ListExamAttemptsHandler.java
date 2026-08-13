package com.schoolmanagement.examination.application.query.listexamattempts;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.schoolmanagement.examination.application.view.ExamAttemptView;
import com.schoolmanagement.examination.domain.AttemptStatus;
import com.schoolmanagement.examination.domain.ExamAttemptRepository;
import com.schoolmanagement.examination.domain.ExamId;

@Service
public class ListExamAttemptsHandler {
  private final ExamAttemptRepository attempts;

  public ListExamAttemptsHandler(ExamAttemptRepository attempts) {
    this.attempts = attempts;
  }

  @Transactional(readOnly = true)
  public List<ExamAttemptView> handle(ListExamAttemptsQuery query) {
    var found = attempts.findByExamId(ExamId.of(query.examId()));

    if (query.flaggedOnly()) {
      found = found.stream().filter(a -> a.status() == AttemptStatus.FLAGGED).toList();
    }

    return found.stream().map(ExamAttemptView::from).toList();
  }
}
