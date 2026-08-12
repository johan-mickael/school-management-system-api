package com.schoolmanagement.student.domain;

import java.util.regex.Pattern;

import com.schoolmanagement.student.domain.exception.InvalidStudentNumber;

public record StudentNumber(String value) {
    private static final Pattern FORMAT = Pattern.compile("^STU-\\d{4}-\\d{4}$");

    public StudentNumber {
        value = (value == null) ? "" : value.trim().toUpperCase();
        if (!FORMAT.matcher(value).matches()) {
            throw new InvalidStudentNumber(value);
        }
    }
}