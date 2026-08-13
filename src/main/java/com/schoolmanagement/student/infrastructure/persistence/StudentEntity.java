package com.schoolmanagement.student.infrastructure.persistence;

import java.time.Instant;
import java.util.UUID;

import com.schoolmanagement.student.domain.StudentStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "students")
public class StudentEntity {

  @Id
  private UUID id;

  @Column(nullable = false, unique = true)
  private String number;

  @Column(name = "first_name", nullable = false)
  private String firstName;

  @Column(name = "last_name", nullable = false)
  private String lastName;

  @Column(nullable = false, unique = true)
  private String email;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private StudentStatus status;

  @Column(name = "enrolled_at", nullable = false)
  private Instant enrolledAt;

  @Column(name = "promotion_id")
  private UUID promotionId;

  protected StudentEntity() {}

  public StudentEntity(
      UUID id,
      String number,
      String firstName,
      String lastName,
      String email,
      StudentStatus status,
      Instant enrolledAt,
      UUID promotionId) {
    this.id = id;
    this.number = number;
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.status = status;
    this.enrolledAt = enrolledAt;
    this.promotionId = promotionId;
  }

  public UUID getId() {
    return id;
  }

  public String getNumber() {
    return number;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public String getEmail() {
    return email;
  }

  public StudentStatus getStatus() {
    return status;
  }

  public Instant getEnrolledAt() {
    return enrolledAt;
  }

  public UUID getPromotionId() {
    return promotionId;
  }
}
