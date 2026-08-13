package com.schoolmanagement.promotion.domain.exception;

import com.schoolmanagement.shared.domain.DomainException;

public final class BlankPromotionName extends DomainException {
    public BlankPromotionName() {
        super("Promotion name must not be blank");
    }
}
