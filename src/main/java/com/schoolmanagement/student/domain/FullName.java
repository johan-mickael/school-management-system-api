package com.schoolmanagement.student.domain;

import com.schoolmanagement.student.domain.exception.BlankStudentName;

public record FullName(String firstName, String lastName) {
    public FullName {
        firstName = normalize(firstName);
        lastName = normalize(lastName);
        if (firstName.isEmpty() || lastName.isEmpty()) {
            throw new BlankStudentName();
        }
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim();
    }

    public String display() {
        return firstName + " " + lastName;
    }
}