package com.schoolmanagement.promotion.domain;

import com.schoolmanagement.promotion.domain.exception.InvalidCapacity;

public record Capacity(int value) {
    public Capacity {
        if (value <= 0) {
            throw new InvalidCapacity(value);
        }
    }
}
