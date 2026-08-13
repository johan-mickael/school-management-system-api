package com.schoolmanagement.examination.infrastructure.persistence;

import org.springframework.stereotype.Component;

import com.schoolmanagement.course.domain.CourseId;
import com.schoolmanagement.examination.domain.Exam;
import com.schoolmanagement.examination.domain.ExamId;
import com.schoolmanagement.promotion.domain.PromotionId;
import com.schoolmanagement.shared.domain.TimeWindow;

@Component
public class ExamMapper {

  public ExamEntity toEntity(Exam e) {
    return new ExamEntity(
        e.id().value(),
        e.courseId().value(),
        e.promotionId().value(),
        e.timeWindow().start(),
        e.timeWindow().end(),
        e.status(),
        e.version());
  }

  public Exam toDomain(ExamEntity e) {
    return Exam.reconstitute(
        new ExamId(e.getId()),
        new CourseId(e.getCourseId()),
        new PromotionId(e.getPromotionId()),
        new TimeWindow(e.getStartsAt(), e.getEndsAt()),
        e.getStatus(),
        e.getVersion());
  }
}
