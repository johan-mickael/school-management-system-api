package com.schoolmanagement.teacher.application.command.hireteacher;

public record HireTeacherCommand(
    String staffNumber,
    String firstName,
    String lastName,
    String email) {
}
