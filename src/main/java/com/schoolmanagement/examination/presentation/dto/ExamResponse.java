package com.schoolmanagement.examination.presentation.dto;

import java.time.Instant;

import com.schoolmanagement.examination.application.view.ExamView;

public record ExamResponse(
    String id,
    String courseId,
    String promotionId,
    Instant start,
    Instant end,
    String status) {

  public static ExamResponse from(ExamView v) {
    return new ExamResponse(
        v.id(),
        v.courseId(),
        v.promotionId(),
        v.start(),
        v.end(),
        v.status());
  }
}
