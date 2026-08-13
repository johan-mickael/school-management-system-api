package com.schoolmanagement.course.infrastructure.persistence;

import org.springframework.stereotype.Component;

import com.schoolmanagement.course.domain.Coefficient;
import com.schoolmanagement.course.domain.Course;
import com.schoolmanagement.course.domain.CourseCode;
import com.schoolmanagement.course.domain.CourseId;
import com.schoolmanagement.course.domain.CourseTitle;
import com.schoolmanagement.promotion.domain.PromotionId;
import com.schoolmanagement.teacher.domain.TeacherId;

@Component
public class CourseMapper {

  public CourseEntity toEntity(Course c) {
    return new CourseEntity(
        c.id().value(),
        c.code().value(),
        c.title().value(),
        c.coefficient().value(),
        c.promotionId().value(),
        c.teacherId() == null ? null : c.teacherId().value());
  }

  public Course toDomain(CourseEntity e) {
    return Course.reconstitute(
        new CourseId(e.getId()),
        new CourseCode(e.getCode()),
        new CourseTitle(e.getTitle()),
        new Coefficient(e.getCoefficient()),
        new PromotionId(e.getPromotionId()),
        e.getTeacherId() == null ? null : new TeacherId(e.getTeacherId()));
  }
}
