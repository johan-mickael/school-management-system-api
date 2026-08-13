package com.schoolmanagement.shared.domain;

import java.time.Instant;

import com.schoolmanagement.shared.domain.exception.InvalidTimeWindow;

public record TimeWindow(Instant start, Instant end) {
    public TimeWindow {
        if (start == null || end == null || !end.isAfter(start)) {
            throw new InvalidTimeWindow(start, end);
        }
    }
}
