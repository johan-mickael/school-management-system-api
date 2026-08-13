package com.schoolmanagement.teacher.infrastructure.persistence;

import java.time.Instant;
import java.util.UUID;

import com.schoolmanagement.teacher.domain.TeacherStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "teachers")
public class TeacherEntity {

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
  private TeacherStatus status;

  @Column(name = "hired_at", nullable = false)
  private Instant hiredAt;

  protected TeacherEntity() {}

  public TeacherEntity(
      UUID id,
      String number,
      String firstName,
      String lastName,
      String email,
      TeacherStatus status,
      Instant hiredAt) {
    this.id = id;
    this.number = number;
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.status = status;
    this.hiredAt = hiredAt;
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

  public TeacherStatus getStatus() {
    return status;
  }

  public Instant getHiredAt() {
    return hiredAt;
  }
}
