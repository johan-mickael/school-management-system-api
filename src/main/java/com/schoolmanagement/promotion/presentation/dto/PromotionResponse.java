package com.schoolmanagement.promotion.presentation.dto;

import com.schoolmanagement.promotion.application.view.PromotionView;

public record PromotionResponse(
    String id,
    String name,
    String academicYear,
    int capacity,
    int occupancy) {

  public static PromotionResponse from(PromotionView v) {
    return new PromotionResponse(
        v.id(),
        v.name(),
        v.academicYear(),
        v.capacity(),
        v.occupancy());
  }
}
