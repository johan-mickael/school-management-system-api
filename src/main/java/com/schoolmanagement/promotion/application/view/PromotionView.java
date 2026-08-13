package com.schoolmanagement.promotion.application.view;

import com.schoolmanagement.promotion.domain.Promotion;

public record PromotionView(
    String id,
    String name,
    String academicYear,
    int capacity,
    int occupancy) {

    public static PromotionView from(Promotion p) {
        return new PromotionView(
                p.id().toString(),
                p.name().value(),
                p.academicYear().value(),
                p.capacity().value(),
                p.occupancy());
    }
}
