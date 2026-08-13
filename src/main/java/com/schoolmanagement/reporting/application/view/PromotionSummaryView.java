package com.schoolmanagement.reporting.application.view;

public record PromotionSummaryView(
    String promotionId,
    int studentCount,
    Double averageAttendanceRate,
    Double averageGrade) {
}
