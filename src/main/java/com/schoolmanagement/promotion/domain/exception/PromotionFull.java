package com.schoolmanagement.promotion.domain.exception;

import com.schoolmanagement.shared.domain.DomainException;
import com.schoolmanagement.promotion.domain.PromotionId;

public final class PromotionFull extends DomainException {
    public PromotionFull(PromotionId id) {
        super("Promotion '%s' is at full capacity".formatted(id));
    }
}
