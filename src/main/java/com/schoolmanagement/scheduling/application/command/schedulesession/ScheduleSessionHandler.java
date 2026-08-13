package com.schoolmanagement.scheduling.application.command.schedulesession;

import java.time.Duration;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.schoolmanagement.course.domain.CourseId;
import com.schoolmanagement.course.domain.CourseRepository;
import com.schoolmanagement.promotion.domain.PromotionId;
import com.schoolmanagement.promotion.domain.PromotionRepository;
import com.schoolmanagement.scheduling.application.view.SessionView;
import com.schoolmanagement.scheduling.domain.GracePeriod;
import com.schoolmanagement.scheduling.domain.Session;
import com.schoolmanagement.scheduling.domain.SessionId;
import com.schoolmanagement.scheduling.domain.SessionRepository;
import com.schoolmanagement.shared.domain.TimeWindow;
import com.schoolmanagement.teacher.domain.TeacherId;
import com.schoolmanagement.teacher.domain.TeacherRepository;

@Service
public class ScheduleSessionHandler {
  private final SessionRepository sessions;
  private final CourseRepository courses;
  private final PromotionRepository promotions;
  private final TeacherRepository teachers;

  public ScheduleSessionHandler(
      SessionRepository sessions,
      CourseRepository courses,
      PromotionRepository promotions,
      TeacherRepository teachers) {
    this.sessions = sessions;
    this.courses = courses;
    this.promotions = promotions;
    this.teachers = teachers;
  }

  @Transactional
  public SessionView handle(ScheduleSessionCommand command) {
    CourseId courseId = CourseId.of(command.courseId());
    courses.getById(courseId);
    PromotionId promotionId = PromotionId.of(command.promotionId());
    promotions.getById(promotionId);
    TeacherId teacherId = TeacherId.of(command.teacherId());
    teachers.getById(teacherId);

    Session session = Session.schedule(
        SessionId.generate(),
        courseId,
        promotionId,
        teacherId,
        new TimeWindow(command.start(), command.end()),
        new GracePeriod(Duration.ofSeconds(command.gracePeriodSeconds())));

    sessions.save(session);

    return SessionView.from(session);
  }
}
