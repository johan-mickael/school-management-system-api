package com.schoolmanagement.attendance.infrastructure.persistence;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.schoolmanagement.attendance.domain.AttendanceId;
import com.schoolmanagement.attendance.domain.AttendanceRecord;
import com.schoolmanagement.attendance.domain.AttendanceRecordRepository;
import com.schoolmanagement.attendance.domain.AttendanceStatus;
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
import com.schoolmanagement.shared.AbstractIntegrationTest;
import com.schoolmanagement.shared.domain.EmailAddress;
import com.schoolmanagement.shared.domain.FullName;
import com.schoolmanagement.shared.domain.TimeWindow;
import com.schoolmanagement.student.domain.Student;
import com.schoolmanagement.student.domain.StudentId;
import com.schoolmanagement.student.domain.StudentNumber;
import com.schoolmanagement.student.domain.StudentRepository;
import com.schoolmanagement.teacher.domain.StaffNumber;
import com.schoolmanagement.teacher.domain.Teacher;
import com.schoolmanagement.teacher.domain.TeacherId;
import com.schoolmanagement.teacher.domain.TeacherRepository;

import java.time.Duration;

import jakarta.persistence.EntityManager;

@Transactional
class AttendanceRecordRepositoryAdapterIT extends AbstractIntegrationTest {

  @Autowired
  private AttendanceRecordRepository attendanceRecords;

  @Autowired
  private EntityManager entityManager;

  @Autowired
  private SessionRepository sessions;

  @Autowired
  private CourseRepository courses;

  @Autowired
  private PromotionRepository promotions;

  @Autowired
  private TeacherRepository teachers;

  @Autowired
  private StudentRepository students;

  private Session aSession() {
    Promotion promotion = Promotion.create(
        PromotionId.generate(), new PromotionName("MSE 2025"), new AcademicYear("2025-2026"), new Capacity(30));
    promotions.save(promotion);
    Teacher teacher = Teacher.hire(
        TeacherId.generate(), new StaffNumber("TCH-2025-0088"), new FullName("Grace", "Hopper"),
        new EmailAddress("grace.attendance@example.com"), Instant.parse("2025-09-01T08:00:00Z"));
    teachers.save(teacher);
    Course course = Course.create(
        CourseId.generate(), new CourseCode("CS301"), new CourseTitle("Networks"),
        new Coefficient(2.0), promotion.id(), teacher.id());
    courses.save(course);
    Session session = Session.schedule(
        SessionId.generate(), course.id(), promotion.id(), teacher.id(),
        new TimeWindow(Instant.parse("2025-09-01T08:00:00Z"), Instant.parse("2025-09-01T10:00:00Z")),
        new GracePeriod(Duration.ofMinutes(15)));
    sessions.save(session);
    return session;
  }

  private Student aStudent() {
    Student student = Student.enroll(
        StudentId.generate(), new StudentNumber("STU-2025-0077"), new FullName("Ada", "Lovelace"),
        new EmailAddress("ada.attendance@example.com"), Instant.parse("2025-09-01T00:00:00Z"));
    students.save(student);
    return student;
  }

  @Test
  void saves_and_retrieves_an_attendance_record() {
    Session session = aSession();
    Student student = aStudent();
    AttendanceRecord record = AttendanceRecord.sign(
        AttendanceId.generate(), session.id(), student.id(),
        Instant.parse("2025-09-01T08:05:00Z"), Instant.parse("2025-09-01T08:00:00Z"), Duration.ofMinutes(15));

    attendanceRecords.save(record);

    AttendanceRecord found = attendanceRecords.findBySessionIdAndStudentId(session.id(), student.id()).orElseThrow();
    assertThat(found.status()).isEqualTo(AttendanceStatus.PRESENT);
  }

  @Test
  void rejects_a_second_record_for_the_same_session_and_student() {
    Session session = aSession();
    Student student = aStudent();
    attendanceRecords.save(AttendanceRecord.sign(
        AttendanceId.generate(), session.id(), student.id(),
        Instant.parse("2025-09-01T08:05:00Z"), Instant.parse("2025-09-01T08:00:00Z"), Duration.ofMinutes(15)));

    org.junit.jupiter.api.Assertions.assertThrows(ConstraintViolationException.class, () -> {
      attendanceRecords.save(AttendanceRecord.absent(AttendanceId.generate(), session.id(), student.id()));
      entityManager.flush();
    });
  }
}
