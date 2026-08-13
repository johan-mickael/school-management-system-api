package com.schoolmanagement.shared.domain;

import com.schoolmanagement.shared.domain.exception.BlankFullName;

public record FullName(String firstName, String lastName) {
    public FullName {
        firstName = normalize(firstName);
        lastName = normalize(lastName);
        if (firstName.isEmpty() || lastName.isEmpty()) {
            throw new BlankFullName();
        }
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim();
    }

    public String display() {
        return firstName + " " + lastName;
    }
}
