// src/main/java/com/schoolmanagement/student/presentation/dto/StudentResponse.java
package com.schoolmanagement.student.presentation.dto;

import java.time.Instant;

import com.schoolmanagement.student.application.view.StudentView;

public record StudentResponse(
    String id,
    String number,
    String firstName,
    String lastName,
    String email,
    String status,
    Instant enrolledAt) {

  public static StudentResponse from(StudentView v) {
    return new StudentResponse(
        v.id(),
        v.number(),
        v.firstName(),
        v.lastName(),
        v.email(),
        v.status(),
        v.enrolledAt());
  }
}
