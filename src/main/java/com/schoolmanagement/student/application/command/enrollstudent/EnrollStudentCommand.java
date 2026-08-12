package com.schoolmanagement.student.application.command.enrollstudent;

public record EnrollStudentCommand(
    String studentNumber,
    String firstName,
    String lastName,
    String email) {
}
