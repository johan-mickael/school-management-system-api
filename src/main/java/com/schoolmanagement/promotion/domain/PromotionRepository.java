package com.schoolmanagement.promotion.domain;

import java.util.List;

import com.schoolmanagement.promotion.domain.exception.PromotionNotFound;

public interface PromotionRepository {
    void save(Promotion promotion);

    /**
     * @throws PromotionNotFound
     */
    Promotion getById(PromotionId id);

    List<Promotion> findArchived();

    List<Promotion> findActive();
}
