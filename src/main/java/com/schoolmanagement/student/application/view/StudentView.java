package com.schoolmanagement.student.application.view;

import java.time.Instant;

import com.schoolmanagement.student.domain.Student;

public record StudentView(
    String id,
    String number,
    String firstName,
    String lastName,
    String email,
    String status,
    Instant enrolledAt) {

    public static StudentView from(Student s) {
        return new StudentView(
                s.id().toString(),
                s.number().value(),
                s.name().firstName(),
                s.name().lastName(),
                s.email().value(),
                s.status().name(),
                s.enrolledAt());
    }
}
