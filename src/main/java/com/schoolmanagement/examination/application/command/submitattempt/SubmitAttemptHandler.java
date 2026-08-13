package com.schoolmanagement.examination.application.command.submitattempt;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.schoolmanagement.examination.application.view.ExamAttemptView;
import com.schoolmanagement.examination.domain.AttemptId;
import com.schoolmanagement.examination.domain.ExamAttempt;
import com.schoolmanagement.examination.domain.ExamAttemptRepository;

@Service
public class SubmitAttemptHandler {
  private final ExamAttemptRepository attempts;

  public SubmitAttemptHandler(ExamAttemptRepository attempts) {
    this.attempts = attempts;
  }

  @Transactional
  public ExamAttemptView handle(SubmitAttemptCommand command) {
    ExamAttempt attempt = attempts.getById(AttemptId.of(command.attemptId()));

    attempt.submit();
    attempts.save(attempt);

    return ExamAttemptView.from(attempt);
  }
}
