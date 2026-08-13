package com.schoolmanagement.examination.infrastructure.persistence;

import java.time.Instant;
import java.util.UUID;

import com.schoolmanagement.examination.domain.ExamStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "exams")
public class ExamEntity {

  @Id
  private UUID id;

  @Column(name = "course_id", nullable = false)
  private UUID courseId;

  @Column(name = "promotion_id", nullable = false)
  private UUID promotionId;

  @Column(name = "starts_at", nullable = false)
  private Instant startsAt;

  @Column(name = "ends_at", nullable = false)
  private Instant endsAt;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ExamStatus status;

  @Version
  @Column(nullable = false)
  private long version;

  protected ExamEntity() {}

  public ExamEntity(
      UUID id,
      UUID courseId,
      UUID promotionId,
      Instant startsAt,
      Instant endsAt,
      ExamStatus status,
      long version) {
    this.id = id;
    this.courseId = courseId;
    this.promotionId = promotionId;
    this.startsAt = startsAt;
    this.endsAt = endsAt;
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

  public Instant getStartsAt() {
    return startsAt;
  }

  public Instant getEndsAt() {
    return endsAt;
  }

  public ExamStatus getStatus() {
    return status;
  }

  public long getVersion() {
    return version;
  }
}
