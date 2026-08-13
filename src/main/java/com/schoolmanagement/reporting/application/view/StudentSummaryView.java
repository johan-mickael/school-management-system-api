package com.schoolmanagement.reporting.application.view;

public record StudentSummaryView(
    String studentId,
    Double attendanceRate,
    Double averageGrade) {
}
