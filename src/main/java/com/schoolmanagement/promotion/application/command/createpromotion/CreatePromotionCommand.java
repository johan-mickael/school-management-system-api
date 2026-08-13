package com.schoolmanagement.promotion.application.command.createpromotion;

public record CreatePromotionCommand(
    String name,
    String academicYear,
    int capacity) {
}
