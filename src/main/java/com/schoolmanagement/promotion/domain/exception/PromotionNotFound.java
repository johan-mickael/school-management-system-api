package com.schoolmanagement.promotion.domain.exception;

import com.schoolmanagement.shared.domain.DomainException;
import com.schoolmanagement.promotion.domain.PromotionId;

public final class PromotionNotFound extends DomainException {
    public PromotionNotFound(PromotionId id) {
        super("Promotion '%s' was not found".formatted(id));
    }
}
