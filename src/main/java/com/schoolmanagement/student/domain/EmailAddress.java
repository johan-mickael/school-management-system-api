package com.schoolmanagement.student.domain;

import java.util.regex.Pattern;

import com.schoolmanagement.student.domain.exception.InvalidEmailAddress;

public record EmailAddress(String value) {
    private static final Pattern FORMAT =
        Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    public EmailAddress {
        value = EmailAddress.normalize(value);
        if (!FORMAT.matcher(value).matches()) {
            throw new InvalidEmailAddress(value);
        }
    }

    private static String normalize(String value) {
        return value == null ? "" : value.toLowerCase();
    }
}