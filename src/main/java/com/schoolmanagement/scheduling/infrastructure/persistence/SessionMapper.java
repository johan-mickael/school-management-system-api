package com.schoolmanagement.scheduling.infrastructure.persistence;

import java.time.Duration;

import org.springframework.stereotype.Component;

import com.schoolmanagement.course.domain.CourseId;
import com.schoolmanagement.promotion.domain.PromotionId;
import com.schoolmanagement.scheduling.domain.GracePeriod;
import com.schoolmanagement.scheduling.domain.Session;
import com.schoolmanagement.scheduling.domain.SessionId;
import com.schoolmanagement.shared.domain.TimeWindow;
import com.schoolmanagement.teacher.domain.TeacherId;

@Component
public class SessionMapper {

  public SessionEntity toEntity(Session s) {
    return new SessionEntity(
        s.id().value(),
        s.courseId().value(),
        s.promotionId().value(),
        s.teacherId().value(),
        s.timeWindow().start(),
        s.timeWindow().end(),
        s.gracePeriod().value().toSeconds(),
        s.status(),
        s.version());
  }

  public Session toDomain(SessionEntity e) {
    return Session.reconstitute(
        new SessionId(e.getId()),
        new CourseId(e.getCourseId()),
        new PromotionId(e.getPromotionId()),
        new TeacherId(e.getTeacherId()),
        new TimeWindow(e.getStartsAt(), e.getEndsAt()),
        new GracePeriod(Duration.ofSeconds(e.getGracePeriodSeconds())),
        e.getStatus(),
        e.getVersion());
  }
}
