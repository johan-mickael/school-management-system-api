package com.schoolmanagement.scheduling.domain;

import java.util.List;

import com.schoolmanagement.promotion.domain.PromotionId;
import com.schoolmanagement.scheduling.domain.exception.SessionNotFound;

public interface SessionRepository {
    void save(Session session);

    /**
     * @throws SessionNotFound
     */
    Session getById(SessionId id);

    List<Session> findByPromotionId(PromotionId promotionId);
}
