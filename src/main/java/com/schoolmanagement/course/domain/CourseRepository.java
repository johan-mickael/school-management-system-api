package com.schoolmanagement.course.domain;

import java.util.List;

import com.schoolmanagement.course.domain.exception.CourseNotFound;
import com.schoolmanagement.promotion.domain.PromotionId;

public interface CourseRepository {
    void save(Course course);

    /**
     * @throws CourseNotFound
     */
    Course getById(CourseId id);

    List<Course> findByPromotionId(PromotionId promotionId);
}
