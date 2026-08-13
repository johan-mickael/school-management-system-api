package com.schoolmanagement.teacher.presentation.dto;

import java.time.Instant;

import com.schoolmanagement.teacher.application.view.TeacherView;

public record TeacherResponse(
    String id,
    String number,
    String firstName,
    String lastName,
    String email,
    String status,
    Instant hiredAt) {

  public static TeacherResponse from(TeacherView v) {
    return new TeacherResponse(
        v.id(),
        v.number(),
        v.firstName(),
        v.lastName(),
        v.email(),
        v.status(),
        v.hiredAt());
  }
}
