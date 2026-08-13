package com.schoolmanagement.examination.application.command.recordintegrityevent;

import java.time.Clock;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.schoolmanagement.examination.application.view.ExamAttemptView;
import com.schoolmanagement.examination.domain.AttemptId;
import com.schoolmanagement.examination.domain.ExamAttempt;
import com.schoolmanagement.examination.domain.ExamAttemptRepository;
import com.schoolmanagement.examination.domain.IntegrityEvent;
import com.schoolmanagement.examination.domain.IntegrityEventType;

@Service
public class RecordIntegrityEventHandler {
  private final ExamAttemptRepository attempts;
  private final Clock clock;
  private final int integrityThreshold;

  public RecordIntegrityEventHandler(
      ExamAttemptRepository attempts,
      Clock clock,
      @Value("${app.exam.integrity-threshold}") int integrityThreshold) {
    this.attempts = attempts;
    this.clock = clock;
    this.integrityThreshold = integrityThreshold;
  }

  @Transactional
  public ExamAttemptView handle(RecordIntegrityEventCommand command) {
    ExamAttempt attempt = attempts.getById(AttemptId.of(command.attemptId()));

    attempt.recordEvent(new IntegrityEvent(
        IntegrityEventType.valueOf(command.eventType()), Instant.now(clock)));

    if (attempt.integrityScore().value() >= integrityThreshold) {
      attempt.flag();
    }

    attempts.save(attempt);

    return ExamAttemptView.from(attempt);
  }
}
