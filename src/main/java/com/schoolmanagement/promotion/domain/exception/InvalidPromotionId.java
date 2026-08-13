package com.schoolmanagement.promotion.domain.exception;

import com.schoolmanagement.shared.domain.DomainException;

public final class InvalidPromotionId extends DomainException {
    public InvalidPromotionId(String raw) {
        super("Invalid promotion id: '%s'".formatted(raw));
    }
}
