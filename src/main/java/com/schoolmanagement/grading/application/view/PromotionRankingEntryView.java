package com.schoolmanagement.grading.application.view;

public record PromotionRankingEntryView(
    String studentId,
    double average,
    int rank) {
}
