package com.schoolmanagement.attendance.domain;

import com.schoolmanagement.attendance.domain.exception.BlankJustification;

public record Justification(String value) {
    public Justification {
        value = (value == null) ? "" : value.trim();
        if (value.isEmpty()) {
            throw new BlankJustification();
        }
    }
}
