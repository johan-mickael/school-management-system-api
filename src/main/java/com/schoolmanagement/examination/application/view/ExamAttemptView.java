package com.schoolmanagement.examination.application.view;

import java.util.List;

import com.schoolmanagement.examination.domain.ExamAttempt;

public record ExamAttemptView(
    String id,
    String examId,
    String studentId,
    String status,
    int integrityScore,
    List<IntegrityEventView> events) {

    public static ExamAttemptView from(ExamAttempt a) {
        return new ExamAttemptView(
                a.id().toString(),
                a.examId().toString(),
                a.studentId().toString(),
                a.status().name(),
                a.integrityScore().value(),
                a.events().stream().map(IntegrityEventView::from).toList());
    }
}
