package com.schoolmanagement.scheduling.infrastructure.persistence;

import java.time.Instant;
import java.util.UUID;

import com.schoolmanagement.scheduling.domain.SessionStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "sessions")
public class SessionEntity {

  @Id
  private UUID id;

  @Column(name = "course_id", nullable = false)
  private UUID courseId;

  @Column(name = "promotion_id", nullable = false)
  private UUID promotionId;

  @Column(name = "teacher_id", nullable = false)
  private UUID teacherId;

  @Column(name = "starts_at", nullable = false)
  private Instant startsAt;

  @Column(name = "ends_at", nullable = false)
  private Instant endsAt;

  @Column(name = "grace_period_seconds", nullable = false)
  private long gracePeriodSeconds;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private SessionStatus status;

  @Version
  @Column(nullable = false)
  private long version;

  protected SessionEntity() {}

  public SessionEntity(
      UUID id,
      UUID courseId,
      UUID promotionId,
      UUID teacherId,
      Instant startsAt,
      Instant endsAt,
      long gracePeriodSeconds,
      SessionStatus status,
      long version) {
    this.id = id;
    this.courseId = courseId;
    this.promotionId = promotionId;
    this.teacherId = teacherId;
    this.startsAt = startsAt;
    this.endsAt = endsAt;
    this.gracePeriodSeconds = gracePeriodSeconds;
    this.status = status;
    this.version = version;
  }

  public UUID getId() {
    return id;
  }

  public UUID getCourseId() {
    return courseId;
  }

  public UUID getPromotionId() {
    return promotionId;
  }

  public UUID getTeacherId() {
    return teacherId;
  }

  public Instant getStartsAt() {
    return startsAt;
  }

  public Instant getEndsAt() {
    return endsAt;
  }

  public long getGracePeriodSeconds() {
    return gracePeriodSeconds;
  }

  public SessionStatus getStatus() {
    return status;
  }

  public long getVersion() {
    return version;
  }
}
