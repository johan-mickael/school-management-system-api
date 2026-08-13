package com.schoolmanagement.course.infrastructure.persistence;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "courses")
public class CourseEntity {

  @Id
  private UUID id;

  @Column(nullable = false, unique = true)
  private String code;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  private double coefficient;

  @Column(name = "promotion_id", nullable = false)
  private UUID promotionId;

  @Column(name = "teacher_id")
  private UUID teacherId;

  protected CourseEntity() {}

  public CourseEntity(
      UUID id,
      String code,
      String title,
      double coefficient,
      UUID promotionId,
      UUID teacherId) {
    this.id = id;
    this.code = code;
    this.title = title;
    this.coefficient = coefficient;
    this.promotionId = promotionId;
    this.teacherId = teacherId;
  }

  public UUID getId() {
    return id;
  }

  public String getCode() {
    return code;
  }

  public String getTitle() {
    return title;
  }

  public double getCoefficient() {
    return coefficient;
  }

  public UUID getPromotionId() {
    return promotionId;
  }

  public UUID getTeacherId() {
    return teacherId;
  }
}
