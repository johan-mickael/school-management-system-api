package com.schoolmanagement.iam.application.command.login;

public record LoginCommand(
    String username,
    String password) {
}
