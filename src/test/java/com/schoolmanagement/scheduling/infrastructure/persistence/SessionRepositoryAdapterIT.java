package com.schoolmanagement.scheduling.infrastructure.persistence;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.transaction.annotation.Transactional;

import com.schoolmanagement.course.domain.Coefficient;
import com.schoolmanagement.course.domain.Course;
import com.schoolmanagement.course.domain.CourseCode;
import com.schoolmanagement.course.domain.CourseId;
import com.schoolmanagement.course.domain.CourseRepository;
import com.schoolmanagement.course.domain.CourseTitle;
import com.schoolmanagement.promotion.domain.AcademicYear;
import com.schoolmanagement.promotion.domain.Capacity;
import com.schoolmanagement.promotion.domain.Promotion;
import com.schoolmanagement.promotion.domain.PromotionId;
import com.schoolmanagement.promotion.domain.PromotionName;
import com.schoolmanagement.promotion.domain.PromotionRepository;
import com.schoolmanagement.scheduling.domain.GracePeriod;
import com.schoolmanagement.scheduling.domain.Session;
import com.schoolmanagement.scheduling.domain.SessionId;
import com.schoolmanagement.scheduling.domain.SessionRepository;
import com.schoolmanagement.scheduling.domain.exception.SessionNotFound;
import com.schoolmanagement.shared.AbstractIntegrationTest;
import com.schoolmanagement.shared.domain.EmailAddress;
import com.schoolmanagement.shared.domain.FullName;
import com.schoolmanagement.shared.domain.TimeWindow;
import com.schoolmanagement.teacher.domain.StaffNumber;
import com.schoolmanagement.teacher.domain.Teacher;
import com.schoolmanagement.teacher.domain.TeacherId;
import com.schoolmanagement.teacher.domain.TeacherRepository;

import java.time.Duration;
import java.util.UUID;

class SessionRepositoryAdapterIT extends AbstractIntegrationTest {

  @Autowired
  private SessionRepository sessions;

  @Autowired
  private CourseRepository courses;

  @Autowired
  private PromotionRepository promotions;

  @Autowired
  private TeacherRepository teachers;

  private Session aScheduledSession() {
    String uniqueSuffix = "%04d".formatted(Math.abs(UUID.randomUUID().hashCode()) % 10000);
    Promotion promotion = Promotion.create(
        PromotionId.generate(), new PromotionName("MSE 2025"), new AcademicYear("2025-2026"), new Capacity(30));
    promotions.save(promotion);
    Teacher teacher = Teacher.hire(
        TeacherId.generate(), new StaffNumber("TCH-2025-" + uniqueSuffix), new FullName("Grace", "Hopper"),
        new EmailAddress("grace.session-" + uniqueSuffix + "@example.com"), Instant.parse("2025-09-01T08:00:00Z"));
    teachers.save(teacher);
    Course course = Course.create(
        CourseId.generate(), new CourseCode("CS-SESSIONADAPTERIT-" + UUID.randomUUID()), new CourseTitle("Databases"),
        new Coefficient(2.0), promotion.id(), teacher.id());
    courses.save(course);

    return Session.schedule(
        SessionId.generate(), course.id(), promotion.id(), teacher.id(),
        new TimeWindow(Instant.parse("2025-09-01T08:00:00Z"), Instant.parse("2025-09-01T10:00:00Z")),
        new GracePeriod(Duration.ofMinutes(15)));
  }

  @Test
  @Transactional
  void saves_and_retrieves_a_session() {
    Session session = aScheduledSession();

    sessions.save(session);

    Session found = sessions.getById(session.id());
    assertThat(found.id()).isEqualTo(session.id());
    assertThat(found.status()).isEqualTo(session.status());
    assertThat(found.timeWindow()).isEqualTo(session.timeWindow());
  }

  @Test
  @Transactional
  void throws_when_session_not_found() {
    assertThatThrownBy(() -> sessions.getById(SessionId.generate()))
        .isInstanceOf(SessionNotFound.class);
  }

  @Test
  void detects_concurrent_transitions_via_optimistic_locking() {
    Session session = aScheduledSession();
    sessions.save(session);

    Session staleCopy = sessions.getById(session.id());
    Session freshCopy = sessions.getById(session.id());

    freshCopy.openSigning();
    sessions.save(freshCopy);

    staleCopy.openSigning();
    assertThatThrownBy(() -> sessions.save(staleCopy))
        .isInstanceOf(ObjectOptimisticLockingFailureException.class);
  }
}
