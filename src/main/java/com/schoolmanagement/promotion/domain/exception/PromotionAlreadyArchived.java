package com.schoolmanagement.promotion.domain.exception;

import com.schoolmanagement.shared.domain.DomainException;
import com.schoolmanagement.promotion.domain.PromotionId;

public final class PromotionAlreadyArchived extends DomainException {
    public PromotionAlreadyArchived(PromotionId id) {
        super("Promotion '%s' is already archived".formatted(id));
    }
}
