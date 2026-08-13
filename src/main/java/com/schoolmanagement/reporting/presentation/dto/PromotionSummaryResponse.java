package com.schoolmanagement.reporting.presentation.dto;

import com.schoolmanagement.reporting.application.view.PromotionSummaryView;

public record PromotionSummaryResponse(
    String promotionId,
    int studentCount,
    Double averageAttendanceRate,
    Double averageGrade) {

  public static PromotionSummaryResponse from(PromotionSummaryView v) {
    return new PromotionSummaryResponse(
        v.promotionId(), v.studentCount(), v.averageAttendanceRate(), v.averageGrade());
  }
}
