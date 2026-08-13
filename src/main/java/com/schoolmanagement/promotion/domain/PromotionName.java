package com.schoolmanagement.promotion.domain;

import com.schoolmanagement.promotion.domain.exception.BlankPromotionName;

public record PromotionName(String value) {
    public PromotionName {
        value = (value == null) ? "" : value.trim();
        if (value.isEmpty()) {
            throw new BlankPromotionName();
        }
    }
}
