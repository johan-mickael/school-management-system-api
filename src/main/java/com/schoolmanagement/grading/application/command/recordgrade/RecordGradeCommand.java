package com.schoolmanagement.grading.application.command.recordgrade;

public record RecordGradeCommand(
    String courseId,
    String studentId,
    String examId,
    double score,
    double coefficient) {
}
