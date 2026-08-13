package com.schoolmanagement.promotion.domain;

import com.schoolmanagement.promotion.domain.exception.PromotionNotFound;

public interface PromotionRepository {
    void save(Promotion promotion);

    /**
     * @throws PromotionNotFound
     */
    Promotion getById(PromotionId id);
}
