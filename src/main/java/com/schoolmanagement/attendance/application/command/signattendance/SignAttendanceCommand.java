package com.schoolmanagement.attendance.application.command.signattendance;

public record SignAttendanceCommand(
    String sessionId,
    String currentUserId) {
}
