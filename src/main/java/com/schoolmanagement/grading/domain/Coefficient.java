package com.schoolmanagement.grading.domain;

import com.schoolmanagement.grading.domain.exception.InvalidCoefficient;

public record Coefficient(double value) {
    public Coefficient {
        if (value <= 0) {
            throw new InvalidCoefficient(value);
        }
    }
}
