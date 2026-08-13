package com.schoolmanagement.grading.domain;

import com.schoolmanagement.grading.domain.exception.InvalidScore;

public record Score(double value) {
    public Score {
        if (value < 0 || value > 20) {
            throw new InvalidScore(value);
        }
    }
}
