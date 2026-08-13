package com.schoolmanagement.teacher.application.view;

import java.time.Instant;

import com.schoolmanagement.teacher.domain.Teacher;

public record TeacherView(
    String id,
    String number,
    String firstName,
    String lastName,
    String email,
    String status,
    Instant hiredAt) {

    public static TeacherView from(Teacher t) {
        return new TeacherView(
                t.id().toString(),
                t.number().value(),
                t.name().firstName(),
                t.name().lastName(),
                t.email().value(),
                t.status().name(),
                t.hiredAt());
    }
}
