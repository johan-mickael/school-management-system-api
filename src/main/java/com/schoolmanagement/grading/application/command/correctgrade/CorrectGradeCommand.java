package com.schoolmanagement.grading.application.command.correctgrade;

public record CorrectGradeCommand(
    String gradeId,
    double newScore) {
}
