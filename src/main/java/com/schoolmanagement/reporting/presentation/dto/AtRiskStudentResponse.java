package com.schoolmanagement.reporting.presentation.dto;

import com.schoolmanagement.reporting.application.view.AtRiskStudentView;

public record AtRiskStudentResponse(
    String studentId,
    Double attendanceRate,
    Double averageGrade) {

  public static AtRiskStudentResponse from(AtRiskStudentView v) {
    return new AtRiskStudentResponse(v.studentId(), v.attendanceRate(), v.averageGrade());
  }
}
