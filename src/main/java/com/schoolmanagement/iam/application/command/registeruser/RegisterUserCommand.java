package com.schoolmanagement.iam.application.command.registeruser;

public record RegisterUserCommand(
    String username,
    String password,
    String role,
    String personId) {
}
