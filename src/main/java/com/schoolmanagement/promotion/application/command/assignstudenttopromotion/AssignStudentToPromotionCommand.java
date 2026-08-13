package com.schoolmanagement.promotion.application.command.assignstudenttopromotion;

public record AssignStudentToPromotionCommand(
    String promotionId,
    String studentId) {
}
