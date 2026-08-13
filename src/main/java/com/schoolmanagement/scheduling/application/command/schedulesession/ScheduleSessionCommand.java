package com.schoolmanagement.scheduling.application.command.schedulesession;

import java.time.Instant;

public record ScheduleSessionCommand(
    String courseId,
    String promotionId,
    String teacherId,
    Instant start,
    Instant end,
    long gracePeriodSeconds) {
}
