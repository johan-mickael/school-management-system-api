package com.schoolmanagement.scheduling.application.view;

import java.time.Instant;

import com.schoolmanagement.scheduling.domain.Session;

public record SessionView(
    String id,
    String courseId,
    String promotionId,
    String teacherId,
    Instant start,
    Instant end,
    long gracePeriodSeconds,
    String status) {

    public static SessionView from(Session s) {
        return new SessionView(
                s.id().toString(),
                s.courseId().toString(),
                s.promotionId().toString(),
                s.teacherId().toString(),
                s.timeWindow().start(),
                s.timeWindow().end(),
                s.gracePeriod().value().toSeconds(),
                s.status().name());
    }
}
