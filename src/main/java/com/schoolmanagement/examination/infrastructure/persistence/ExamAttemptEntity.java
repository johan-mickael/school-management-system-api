package com.schoolmanagement.examination.infrastructure.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.schoolmanagement.examination.domain.AttemptStatus;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "exam_attempts")
public class ExamAttemptEntity {

  @Id
  private UUID id;

  @Column(name = "exam_id", nullable = false)
  private UUID examId;

  @Column(name = "student_id", nullable = false)
  private UUID studentId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private AttemptStatus status;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "integrity_events", joinColumns = @JoinColumn(name = "attempt_id"))
  @OrderColumn(name = "event_order")
  private List<IntegrityEventEmbeddable> events = new ArrayList<>();

  @Version
  @Column(nullable = false)
  private long version;

  protected ExamAttemptEntity() {}

  public ExamAttemptEntity(
      UUID id,
      UUID examId,
      UUID studentId,
      AttemptStatus status,
      List<IntegrityEventEmbeddable> events,
      long version) {
    this.id = id;
    this.examId = examId;
    this.studentId = studentId;
    this.status = status;
    this.events = events;
    this.version = version;
  }

  public UUID getId() {
    return id;
  }

  public UUID getExamId() {
    return examId;
  }

  public UUID getStudentId() {
    return studentId;
  }

  public AttemptStatus getStatus() {
    return status;
  }

  public List<IntegrityEventEmbeddable> getEvents() {
    return events;
  }

  public long getVersion() {
    return version;
  }
}
