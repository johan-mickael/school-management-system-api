package com.schoolmanagement.examination.application.command.startattempt;

public record StartAttemptCommand(
    String examId,
    String currentUserId) {
}
