package com.schoolmanagement.scheduling.presentation.dto;

import java.time.Instant;

import com.schoolmanagement.scheduling.application.view.SessionView;

public record SessionResponse(
    String id,
    String courseId,
    String promotionId,
    String teacherId,
    Instant start,
    Instant end,
    long gracePeriodSeconds,
    String status) {

  public static SessionResponse from(SessionView v) {
    return new SessionResponse(
        v.id(),
        v.courseId(),
        v.promotionId(),
        v.teacherId(),
        v.start(),
        v.end(),
        v.gracePeriodSeconds(),
        v.status());
  }
}
