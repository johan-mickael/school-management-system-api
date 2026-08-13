package com.schoolmanagement.promotion.domain.exception;

import com.schoolmanagement.shared.domain.DomainException;
import com.schoolmanagement.promotion.domain.PromotionId;

public final class PromotionNotOccupied extends DomainException {
    public PromotionNotOccupied(PromotionId id) {
        super("Promotion '%s' has no students to release".formatted(id));
    }
}
