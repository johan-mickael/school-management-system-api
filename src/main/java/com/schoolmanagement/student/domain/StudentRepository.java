package com.schoolmanagement.student.domain;

import java.util.List;

import com.schoolmanagement.promotion.domain.PromotionId;
import com.schoolmanagement.student.domain.exception.StudentNotFound;

public interface StudentRepository {
    void save(Student student);

    /**
     * @throws StudentNotFound
     */
    Student getById(StudentId id);

    List<Student> findByPromotionId(PromotionId promotionId);

    /**
     * Same as {@link #findByPromotionId}, excluding archived students.
     */
    List<Student> findActiveByPromotionId(PromotionId promotionId);
}