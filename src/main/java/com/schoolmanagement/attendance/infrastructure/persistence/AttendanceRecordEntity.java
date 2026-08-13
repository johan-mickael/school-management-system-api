package com.schoolmanagement.attendance.infrastructure.persistence;

import java.time.Instant;
import java.util.UUID;

import com.schoolmanagement.attendance.domain.AttendanceStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "attendance_records")
public class AttendanceRecordEntity {

  @Id
  private UUID id;

  @Column(name = "session_id", nullable = false)
  private UUID sessionId;

  @Column(name = "student_id", nullable = false)
  private UUID studentId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private AttendanceStatus status;

  @Column(name = "signed_at")
  private Instant signedAt;

  @Column(name = "justification")
  private String justification;

  protected AttendanceRecordEntity() {}

  public AttendanceRecordEntity(
      UUID id,
      UUID sessionId,
      UUID studentId,
      AttendanceStatus status,
      Instant signedAt,
      String justification) {
    this.id = id;
    this.sessionId = sessionId;
    this.studentId = studentId;
    this.status = status;
    this.signedAt = signedAt;
    this.justification = justification;
  }

  public UUID getId() {
    return id;
  }

  public UUID getSessionId() {
    return sessionId;
  }

  public UUID getStudentId() {
    return studentId;
  }

  public AttendanceStatus getStatus() {
    return status;
  }

  public Instant getSignedAt() {
    return signedAt;
  }

  public String getJustification() {
    return justification;
  }
}
