package com.schoolmanagement.course.domain;

import com.schoolmanagement.course.domain.exception.InvalidCoefficient;

public record Coefficient(double value) {
    public Coefficient {
        if (value <= 0) {
            throw new InvalidCoefficient(value);
        }
    }
}
