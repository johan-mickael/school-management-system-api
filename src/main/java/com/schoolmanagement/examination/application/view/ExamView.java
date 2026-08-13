package com.schoolmanagement.examination.application.view;

import java.time.Instant;

import com.schoolmanagement.examination.domain.Exam;

public record ExamView(
    String id,
    String courseId,
    String promotionId,
    Instant start,
    Instant end,
    String status) {

    public static ExamView from(Exam e) {
        return new ExamView(
                e.id().toString(),
                e.courseId().toString(),
                e.promotionId().toString(),
                e.timeWindow().start(),
                e.timeWindow().end(),
                e.status().name());
    }
}
