package com.schoolmanagement.iam.presentation;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.schoolmanagement.course.domain.Course;
import com.schoolmanagement.course.domain.CourseCode;
import com.schoolmanagement.course.domain.CourseId;
import com.schoolmanagement.course.domain.CourseRepository;
import com.schoolmanagement.course.domain.CourseTitle;
import com.schoolmanagement.grading.domain.Coefficient;
import com.schoolmanagement.grading.domain.Grade;
import com.schoolmanagement.grading.domain.GradeId;
import com.schoolmanagement.grading.domain.GradeRepository;
import com.schoolmanagement.grading.domain.Score;
import com.schoolmanagement.iam.domain.PasswordHasher;
import com.schoolmanagement.iam.domain.PersonId;
import com.schoolmanagement.iam.domain.Role;
import com.schoolmanagement.iam.domain.User;
import com.schoolmanagement.iam.domain.UserId;
import com.schoolmanagement.iam.domain.UserRepository;
import com.schoolmanagement.iam.domain.Username;
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
import com.schoolmanagement.student.domain.StudentNumber;
import com.schoolmanagement.student.domain.StudentRepository;
import com.schoolmanagement.teacher.domain.StaffNumber;
import com.schoolmanagement.teacher.domain.Teacher;
import com.schoolmanagement.teacher.domain.TeacherId;
import com.schoolmanagement.teacher.domain.TeacherRepository;

@AutoConfigureMockMvc
@Transactional
class AuthorizationOwnershipIT extends AbstractIntegrationTest {

  private static final Pattern TOKEN_FIELD = Pattern.compile("\"token\":\"([^\"]+)\"");

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private UserRepository users;

  @Autowired
  private PasswordHasher passwordHasher;

  @Autowired
  private PromotionRepository promotions;

  @Autowired
  private TeacherRepository teachers;

  @Autowired
  private CourseRepository courses;

  @Autowired
  private SessionRepository sessions;

  @Autowired
  private StudentRepository students;

  @Autowired
  private GradeRepository grades;

  private Promotion aPromotion() {
    Promotion promotion = Promotion.create(
        PromotionId.generate(), new PromotionName("MSE 2025"), new AcademicYear("2025-2026"), new Capacity(30));
    promotions.save(promotion);
    return promotion;
  }

  private Teacher aTeacher(String staffNumber) {
    Teacher teacher = Teacher.hire(
        TeacherId.generate(), new StaffNumber(staffNumber),
        new FullName("Ada", "Lovelace"), new EmailAddress(staffNumber.toLowerCase() + "@example.com"),
        Instant.parse("2025-08-01T00:00:00Z"));
    teachers.save(teacher);
    return teacher;
  }

  private Course aCourse(PromotionId promotionId, String code, TeacherId teacherId) {
    Course course = Course.create(
        CourseId.generate(), new CourseCode(code), new CourseTitle("Title " + code),
        new com.schoolmanagement.course.domain.Coefficient(1.0), promotionId, teacherId);
    courses.save(course);
    return course;
  }

  private Session aSession(Course course, PromotionId promotionId, TeacherId teacherId) {
    Session session = Session.schedule(
        SessionId.generate(), course.id(), promotionId, teacherId,
        new TimeWindow(Instant.parse("2025-10-01T08:00:00Z"), Instant.parse("2025-10-01T10:00:00Z")),
        new GracePeriod(Duration.ofMinutes(10)));
    sessions.save(session);
    return session;
  }

  private void aUser(String username, Role role, TeacherId teacherId) {
    users.save(User.register(
        UserId.generate(), new Username(username), passwordHasher.hash("secret123"), role,
        teacherId == null ? null : new PersonId(teacherId.value())));
  }

  private String login(String username) throws Exception {
    String body = mockMvc.perform(post("/api/v1/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"username\":\"%s\",\"password\":\"secret123\"}".formatted(username)))
        .andReturn().getResponse().getContentAsString();

    Matcher matcher = TOKEN_FIELD.matcher(body);
    if (!matcher.find()) {
      throw new IllegalStateException("token not found in response: " + body);
    }
    return matcher.group(1);
  }

  @Test
  void admin_can_open_signing_for_a_session_taught_by_someone_else() throws Exception {
    Promotion promotion = aPromotion();
    Teacher owner = aTeacher("TCH-2025-0001");
    Course course = aCourse(promotion.id(), "CS801", owner.id());
    Session session = aSession(course, promotion.id(), owner.id());
    aUser("admin.owner-it", Role.ADMIN, null);

    mockMvc.perform(post("/api/v1/sessions/" + session.id() + "/open")
        .header("Authorization", "Bearer " + login("admin.owner-it")))
        .andExpect(status().isOk());
  }

  @Test
  void teacher_who_owns_the_session_can_open_signing() throws Exception {
    Promotion promotion = aPromotion();
    Teacher owner = aTeacher("TCH-2025-0002");
    Course course = aCourse(promotion.id(), "CS802", owner.id());
    Session session = aSession(course, promotion.id(), owner.id());
    aUser("teacher.owner-it", Role.TEACHER, owner.id());

    mockMvc.perform(post("/api/v1/sessions/" + session.id() + "/open")
        .header("Authorization", "Bearer " + login("teacher.owner-it")))
        .andExpect(status().isOk());
  }

  @Test
  void teacher_who_does_not_own_the_session_is_forbidden() throws Exception {
    Promotion promotion = aPromotion();
    Teacher owner = aTeacher("TCH-2025-0003");
    Teacher other = aTeacher("TCH-2025-0004");
    Course course = aCourse(promotion.id(), "CS803", owner.id());
    Session session = aSession(course, promotion.id(), owner.id());
    aUser("teacher.other-it", Role.TEACHER, other.id());

    mockMvc.perform(post("/api/v1/sessions/" + session.id() + "/open")
        .header("Authorization", "Bearer " + login("teacher.other-it")))
        .andExpect(status().isForbidden());
  }

  @Test
  void opening_signing_for_a_nonexistent_session_is_not_found_not_forbidden() throws Exception {
    Teacher owner = aTeacher("TCH-2025-0005");
    aUser("teacher.missing-it", Role.TEACHER, owner.id());

    mockMvc.perform(post("/api/v1/sessions/" + UUID.randomUUID() + "/open")
        .header("Authorization", "Bearer " + login("teacher.missing-it")))
        .andExpect(status().isNotFound());
  }

  @Test
  void teacher_who_teaches_the_grades_course_can_correct_it() throws Exception {
    Promotion promotion = aPromotion();
    Teacher owner = aTeacher("TCH-2025-0006");
    Course course = aCourse(promotion.id(), "CS804", owner.id());
    Student student = Student.enroll(
        com.schoolmanagement.student.domain.StudentId.generate(), new StudentNumber("STU-2025-0801"),
        new FullName("Grace", "Hopper"), new EmailAddress("grace-0801@example.com"),
        Instant.parse("2025-09-01T00:00:00Z"));
    students.save(student);
    Grade grade = Grade.record(
        GradeId.generate(), student.id(), course.id(), null, new Score(12.0), new Coefficient(1.0));
    grades.save(grade);
    aUser("teacher.grade-owner-it", Role.TEACHER, owner.id());

    mockMvc.perform(post("/api/v1/grades/" + grade.id() + "/correct")
        .header("Authorization", "Bearer " + login("teacher.grade-owner-it"))
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {"score":15.0}"""))
        .andExpect(status().isOk());
  }

  @Test
  void teacher_who_does_not_teach_the_grades_course_is_forbidden() throws Exception {
    Promotion promotion = aPromotion();
    Teacher owner = aTeacher("TCH-2025-0007");
    Teacher other = aTeacher("TCH-2025-0008");
    Course course = aCourse(promotion.id(), "CS805", owner.id());
    Student student = Student.enroll(
        com.schoolmanagement.student.domain.StudentId.generate(), new StudentNumber("STU-2025-0802"),
        new FullName("Grace", "Hopper"), new EmailAddress("grace-0802@example.com"),
        Instant.parse("2025-09-01T00:00:00Z"));
    students.save(student);
    Grade grade = Grade.record(
        GradeId.generate(), student.id(), course.id(), null, new Score(12.0), new Coefficient(1.0));
    grades.save(grade);
    aUser("teacher.grade-other-it", Role.TEACHER, other.id());

    mockMvc.perform(post("/api/v1/grades/" + grade.id() + "/correct")
        .header("Authorization", "Bearer " + login("teacher.grade-other-it"))
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
            {"score":15.0}"""))
        .andExpect(status().isForbidden());
  }
}
