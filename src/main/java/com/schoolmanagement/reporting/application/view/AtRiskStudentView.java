package com.schoolmanagement.reporting.application.view;

public record AtRiskStudentView(
    String studentId,
    Double attendanceRate,
    Double averageGrade) {
}
