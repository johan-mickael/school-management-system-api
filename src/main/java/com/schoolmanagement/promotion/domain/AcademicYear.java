package com.schoolmanagement.promotion.domain;

import java.util.regex.Pattern;

import com.schoolmanagement.promotion.domain.exception.InvalidAcademicYear;

public record AcademicYear(String value) {
    private static final Pattern FORMAT = Pattern.compile("^(\\d{4})-(\\d{4})$");

    public AcademicYear {
        value = (value == null) ? "" : value.trim();
        var matcher = FORMAT.matcher(value);
        if (!matcher.matches()) {
            throw new InvalidAcademicYear(value);
        }

        int startYear = Integer.parseInt(matcher.group(1));
        int endYear = Integer.parseInt(matcher.group(2));
        if (endYear != startYear + 1) {
            throw new InvalidAcademicYear(value);
        }
    }
}
