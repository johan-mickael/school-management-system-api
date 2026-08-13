package com.schoolmanagement.grading.application.view;

import com.schoolmanagement.grading.domain.Grade;

public record GradeView(
    String id,
    String studentId,
    String courseId,
    String examId,
    double score,
    double coefficient) {

    public static GradeView from(Grade g) {
        return new GradeView(
                g.id().toString(),
                g.studentId().toString(),
                g.courseId().toString(),
                g.examId() == null ? null : g.examId().toString(),
                g.score().value(),
                g.coefficient().value());
    }
}
