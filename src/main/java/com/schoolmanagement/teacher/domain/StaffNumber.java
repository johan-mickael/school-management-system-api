package com.schoolmanagement.teacher.domain;

import java.util.regex.Pattern;

import com.schoolmanagement.teacher.domain.exception.InvalidStaffNumber;

public record StaffNumber(String value) {
    private static final Pattern FORMAT = Pattern.compile("^TCH-\\d{4}-\\d{4}$");

    public StaffNumber {
        value = (value == null) ? "" : value.trim().toUpperCase();
        if (!FORMAT.matcher(value).matches()) {
            throw new InvalidStaffNumber(value);
        }
    }
}
