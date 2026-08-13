package com.schoolmanagement.reporting.application;

import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.schoolmanagement.attendance.domain.AttendanceId;
import com.schoolmanagement.attendance.domain.AttendanceRecord;
import com.schoolmanagement.attendance.domain.AttendanceRecordRepository;
import com.schoolmanagement.course.domain.Coefficient;
import com.schoolmanagement.course.domain.Course;
import com.schoolmanagement.course.domain.CourseCode;
import com.schoolmanagement.course.domain.CourseId;
import com.schoolmanagement.course.domain.CourseRepository;
import com.schoolmanagement.course.domain.CourseTitle;
import com.schoolmanagement.grading.application.command.recordgrade.RecordGradeCommand;
import com.schoolmanagement.grading.application.command.recordgrade.RecordGradeHandler;
import com.schoolmanagement.promotion.domain.AcademicYear;
import com.schoolmanagement.promotion.domain.Capacity;
import com.schoolmanagement.promotion.domain.Promotion;
import com.schoolmanagement.promotion.domain.PromotionId;
import com.schoolmanagement.promotion.domain.PromotionName;
import com.schoolmanagement.promotion.domain.PromotionRepository;
import com.schoolmanagement.reporting.application.query.getatriskstudents.GetAtRiskStudentsHandler;
import com.schoolmanagement.reporting.application.query.getatriskstudents.GetAtRiskStudentsQuery;
import com.schoolmanagement.reporting.application.query.getpromotionsummary.GetPromotionSummaryHandler;
import com.schoolmanagement.reporting.application.query.getpromotionsummary.GetPromotionSummaryQuery;
import com.schoolmanagement.reporting.application.query.getstudentsummary.GetStudentSummaryHandler;
import com.schoolmanagement.reporting.application.query.getstudentsummary.GetStudentSummaryQuery;
import com.schoolmanagement.reporting.application.view.AtRiskStudentView;
import com.schoolmanagement.reporting.application.view.PromotionSummaryView;
import com.schoolmanagement.reporting.application.view.StudentSummaryView;
import com.schoolmanagement.scheduling.domain.GracePeriod;
import com.schoolmanagement.scheduling.domain.Session;
import com.schoolmanagement.scheduling.domain.SessionId;
import com.schoolmanagement.scheduling.domain.SessionRepository;
import com.schoolmanagement.shared.AbstractIntegrationTest;
import com.schoolmanagement.shared.domain.EmailAddress;
import com.schoolmanagement.shared.domain.FullName;
import com.schoolmanagement.shared.domain.TimeWindow;
import com.schoolmanagement.student.domain.Student;
import com.schoolmanagement.student.domain.StudentNumber;
import com.schoolmanagement.student.domain.StudentRepository;
import com.schoolmanagement.teacher.domain.StaffNumber;
import com.schoolmanagement.teacher.domain.Teacher;
import com.schoolmanagement.teacher.domain.TeacherId;
import com.schoolmanagement.teacher.domain.TeacherRepository;

@Transactional
class ReportingIT extends AbstractIntegrationTest {

  @Autowired
  private GetStudentSummaryHandler studentSummary;

  @Autowired
  private GetPromotionSummaryHandler promotionSummary;

  @Autowired
  private GetAtRiskStudentsHandler atRiskStudents;

  @Autowired
  private RecordGradeHandler recordGrade;

  @Autowired
  private PromotionRepository promotions;

  @Autowired
  private StudentRepository students;

  @Autowired
  private CourseRepository courses;

  @Autowired
  private TeacherRepository teachers;

  @Autowired
  private SessionRepository sessions;

  @Autowired
  private AttendanceRecordRepository attendanceRecords;

  private Promotion aPromotion() {
    Promotion promotion = Promotion.create(
        PromotionId.generate(), new PromotionName("MSE 2025"), new AcademicYear("2025-2026"), new Capacity(30));
    promotions.save(promotion);
    return promotion;
  }

  private Student aStudent(String number, Promotion promotion) {
    Student student = Student.enroll(
        com.schoolmanagement.student.domain.StudentId.generate(), new StudentNumber(number),
        new FullName("Ada", "Lovelace"), new EmailAddress(number.toLowerCase() + "@example.com"),
        Instant.parse("2025-09-01T00:00:00Z"));
    student.assignToPromotion(promotion.id());
    students.save(student);
    return student;
  }

  private Course aCourse(PromotionId promotionId, String code) {
    Course course = Course.create(
        CourseId.generate(), new CourseCode(code), new CourseTitle("Title " + code),
        new Coefficient(1.0), promotionId, null);
    courses.save(course);
    return course;
  }

  private Session aSession(Course course, Promotion promotion, Teacher teacher) {
    Session session = Session.schedule(
        SessionId.generate(), course.id(), promotion.id(), teacher.id(),
        new TimeWindow(Instant.parse("2025-10-01T08:00:00Z"), Instant.parse("2025-10-01T10:00:00Z")),
        new GracePeriod(Duration.ofMinutes(10)));
    sessions.save(session);
    return session;
  }

  private Teacher aTeacher() {
    Teacher teacher = Teacher.hire(
        TeacherId.generate(), new StaffNumber("STAFF-0001"),
        new FullName("Alan", "Turing"), new EmailAddress("alan@example.com"), Instant.parse("2025-08-01T00:00:00Z"));
    teachers.save(teacher);
    return teacher;
  }

  @Test
  void student_summary_composes_attendance_and_grade_averages() {
    Promotion promotion = aPromotion();
    Student student = aStudent("STU-2025-0501", promotion);
    Course course = aCourse(promotion.id(), "CS701");
    Teacher teacher = aTeacher();
    Session session = aSession(course, promotion, teacher);

    attendanceRecords.save(AttendanceRecord.absent(AttendanceId.generate(), session.id(), student.id()));
    recordGrade.handle(new RecordGradeCommand(course.id().toString(), student.id().toString(), null, 14.0, 1.0));

    StudentSummaryView summary = studentSummary.handle(new GetStudentSummaryQuery(student.id().toString()));

    assertThat(summary.attendanceRate()).isEqualTo(0.0);
    assertThat(summary.averageGrade()).isEqualTo(14.0);
  }

  @Test
  void promotion_summary_averages_across_students_with_data() {
    Promotion promotion = aPromotion();
    Course course = aCourse(promotion.id(), "CS702");
    Teacher teacher = aTeacher();
    Session session = aSession(course, promotion, teacher);

    Student withData = aStudent("STU-2025-0502", promotion);
    attendanceRecords.save(AttendanceRecord.sign(
        AttendanceId.generate(), session.id(), withData.id(),
        Instant.parse("2025-10-01T08:00:00Z"), session.timeWindow().start(), Duration.ofMinutes(10)));
    recordGrade.handle(new RecordGradeCommand(course.id().toString(), withData.id().toString(), null, 12.0, 1.0));

    aStudent("STU-2025-0503", promotion);

    PromotionSummaryView summary =
        promotionSummary.handle(new GetPromotionSummaryQuery(promotion.id().toString()));

    assertThat(summary.studentCount()).isEqualTo(2);
    assertThat(summary.averageAttendanceRate()).isEqualTo(1.0);
    assertThat(summary.averageGrade()).isEqualTo(12.0);
  }

  @Test
  void at_risk_students_are_flagged_by_configured_thresholds() {
    Promotion promotion = aPromotion();
    Course course = aCourse(promotion.id(), "CS703");
    Teacher teacher = aTeacher();
    Session session = aSession(course, promotion, teacher);

    Student atRisk = aStudent("STU-2025-0504", promotion);
    attendanceRecords.save(AttendanceRecord.absent(AttendanceId.generate(), session.id(), atRisk.id()));
    recordGrade.handle(new RecordGradeCommand(course.id().toString(), atRisk.id().toString(), null, 5.0, 1.0));

    Student healthy = aStudent("STU-2025-0505", promotion);
    attendanceRecords.save(AttendanceRecord.sign(
        AttendanceId.generate(), session.id(), healthy.id(),
        Instant.parse("2025-10-01T08:00:00Z"), session.timeWindow().start(), Duration.ofMinutes(10)));
    recordGrade.handle(new RecordGradeCommand(course.id().toString(), healthy.id().toString(), null, 16.0, 1.0));

    java.util.List<AtRiskStudentView> atRiskList =
        atRiskStudents.handle(new GetAtRiskStudentsQuery(promotion.id().toString()));

    assertThat(atRiskList).extracting(AtRiskStudentView::studentId).containsExactly(atRisk.id().toString());
  }
}
