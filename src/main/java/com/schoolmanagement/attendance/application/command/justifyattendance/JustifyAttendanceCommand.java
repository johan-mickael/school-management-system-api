package com.schoolmanagement.attendance.application.command.justifyattendance;

public record JustifyAttendanceCommand(
    String sessionId,
    String studentId,
    String justification) {
}
