package com.schoolmanagement.promotion.domain;

import java.util.UUID;

import com.schoolmanagement.promotion.domain.exception.InvalidPromotionId;

public record PromotionId(UUID value) {
    public PromotionId {
        if (null == value) {
            throw new InvalidPromotionId("null");
        }
    }

    public static PromotionId generate() {
        return new PromotionId(UUID.randomUUID());
    }

    public static PromotionId of(String raw) {
        try {
            return new PromotionId(UUID.fromString(raw));
        } catch (IllegalArgumentException e) {
            throw new InvalidPromotionId(raw);
        }
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
