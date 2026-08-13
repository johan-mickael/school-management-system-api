package com.schoolmanagement.examination.application.query.getattempt;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.schoolmanagement.examination.application.view.ExamAttemptView;
import com.schoolmanagement.examination.domain.AttemptId;
import com.schoolmanagement.examination.domain.ExamAttempt;
import com.schoolmanagement.examination.domain.ExamAttemptRepository;

@Service
public class GetAttemptHandler {
  private final ExamAttemptRepository attempts;

  public GetAttemptHandler(ExamAttemptRepository attempts) {
    this.attempts = attempts;
  }

  @Transactional(readOnly = true)
  public ExamAttemptView handle(GetAttemptQuery query) {
    ExamAttempt foundAttempt = attempts.getById(AttemptId.of(query.attemptId()));

    return ExamAttemptView.from(foundAttempt);
  }
}
