package com.schoolmanagement.reporting.presentation.dto;

import com.schoolmanagement.reporting.application.view.StudentSummaryView;

public record StudentSummaryResponse(
    String studentId,
    Double attendanceRate,
    Double averageGrade) {

  public static StudentSummaryResponse from(StudentSummaryView v) {
    return new StudentSummaryResponse(v.studentId(), v.attendanceRate(), v.averageGrade());
  }
}
