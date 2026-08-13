package com.schoolmanagement.grading.infrastructure.persistence;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "grades")
public class GradeEntity {

  @Id
  private UUID id;

  @Column(name = "student_id", nullable = false)
  private UUID studentId;

  @Column(name = "course_id", nullable = false)
  private UUID courseId;

  @Column(name = "exam_id")
  private UUID examId;

  @Column(nullable = false)
  private double score;

  @Column(nullable = false)
  private double coefficient;

  protected GradeEntity() {}

  public GradeEntity(
      UUID id,
      UUID studentId,
      UUID courseId,
      UUID examId,
      double score,
      double coefficient) {
    this.id = id;
    this.studentId = studentId;
    this.courseId = courseId;
    this.examId = examId;
    this.score = score;
    this.coefficient = coefficient;
  }

  public UUID getId() {
    return id;
  }

  public UUID getStudentId() {
    return studentId;
  }

  public UUID getCourseId() {
    return courseId;
  }

  public UUID getExamId() {
    return examId;
  }

  public double getScore() {
    return score;
  }

  public double getCoefficient() {
    return coefficient;
  }
}
