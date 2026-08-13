package com.schoolmanagement.scheduling.domain;

import java.time.Duration;

import com.schoolmanagement.scheduling.domain.exception.InvalidGracePeriod;

public record GracePeriod(Duration value) {
    public GracePeriod {
        if (value == null || value.isNegative()) {
            throw new InvalidGracePeriod(value);
        }
    }
}
