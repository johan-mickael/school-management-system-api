package com.schoolmanagement.grading.presentation.dto;

import com.schoolmanagement.grading.application.view.PromotionRankingEntryView;

public record PromotionRankingEntryResponse(String studentId, double average, int rank) {

  public static PromotionRankingEntryResponse from(PromotionRankingEntryView v) {
    return new PromotionRankingEntryResponse(v.studentId(), v.average(), v.rank());
  }
}
