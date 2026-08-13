package com.schoolmanagement.examination.domain;

import java.util.List;

import com.schoolmanagement.examination.domain.exception.ExamNotFound;
import com.schoolmanagement.promotion.domain.PromotionId;

public interface ExamRepository {
    void save(Exam exam);

    /**
     * @throws ExamNotFound
     */
    Exam getById(ExamId id);

    List<Exam> findByPromotionId(PromotionId promotionId);
}
