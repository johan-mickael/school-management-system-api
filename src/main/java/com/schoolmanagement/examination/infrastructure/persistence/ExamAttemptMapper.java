package com.schoolmanagement.examination.infrastructure.persistence;

import org.springframework.stereotype.Component;

import com.schoolmanagement.examination.domain.AttemptId;
import com.schoolmanagement.examination.domain.ExamAttempt;
import com.schoolmanagement.examination.domain.ExamId;
import com.schoolmanagement.examination.domain.IntegrityEvent;
import com.schoolmanagement.student.domain.StudentId;

@Component
public class ExamAttemptMapper {

  public ExamAttemptEntity toEntity(ExamAttempt a) {
    return new ExamAttemptEntity(
        a.id().value(),
        a.examId().value(),
        a.studentId().value(),
        a.status(),
        a.events().stream()
            .map(e -> new IntegrityEventEmbeddable(e.type(), e.occurredAt()))
            .toList(),
        a.version());
  }

  public ExamAttempt toDomain(ExamAttemptEntity e) {
    return ExamAttempt.reconstitute(
        new AttemptId(e.getId()),
        new ExamId(e.getExamId()),
        new StudentId(e.getStudentId()),
        e.getEvents().stream()
            .map(ev -> new IntegrityEvent(ev.getType(), ev.getOccurredAt()))
            .toList(),
        e.getStatus(),
        e.getVersion());
  }
}
