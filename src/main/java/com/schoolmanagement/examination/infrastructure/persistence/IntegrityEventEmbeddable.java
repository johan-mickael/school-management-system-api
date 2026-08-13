package com.schoolmanagement.examination.infrastructure.persistence;

import java.time.Instant;

import com.schoolmanagement.examination.domain.IntegrityEventType;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Embeddable
public class IntegrityEventEmbeddable {

  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false)
  private IntegrityEventType type;

  @Column(name = "occurred_at", nullable = false)
  private Instant occurredAt;

  protected IntegrityEventEmbeddable() {}

  public IntegrityEventEmbeddable(IntegrityEventType type, Instant occurredAt) {
    this.type = type;
    this.occurredAt = occurredAt;
  }

  public IntegrityEventType getType() {
    return type;
  }

  public Instant getOccurredAt() {
    return occurredAt;
  }
}
