package com.schoolmanagement.examination.application.command.recordintegrityevent;

public record RecordIntegrityEventCommand(
    String attemptId,
    String eventType) {
}
