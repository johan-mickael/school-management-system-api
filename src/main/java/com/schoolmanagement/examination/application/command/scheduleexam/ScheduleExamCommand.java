package com.schoolmanagement.examination.application.command.scheduleexam;

import java.time.Instant;

public record ScheduleExamCommand(
    String courseId,
    String promotionId,
    Instant start,
    Instant end) {
}
