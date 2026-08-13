package com.schoolmanagement.student.application;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;

import com.schoolmanagement.promotion.domain.PromotionId;
import com.schoolmanagement.student.application.command.enrollstudent.EnrollStudentCommand;
import com.schoolmanagement.student.application.command.enrollstudent.EnrollStudentHandler;
import com.schoolmanagement.student.application.view.StudentView;
import com.schoolmanagement.student.domain.Student;
import com.schoolmanagement.student.domain.StudentId;
import com.schoolmanagement.student.domain.StudentRepository;
import com.schoolmanagement.student.domain.exception.InvalidStudentNumber;
import com.schoolmanagement.student.domain.exception.StudentNotFound;

class EnrollStudentHandlerTest {
  static class InMemoryStudents implements StudentRepository {
    final Map<java.util.UUID, Student> db = new HashMap<>();

    public void save(Student s) {
      db.put(s.id().value(), s);
    }

    public Student getById(StudentId id) {
      Student s = db.get(id.value());
      if (s == null)
        throw new StudentNotFound(id);
      return s;
    }

    public List<Student> findByPromotionId(PromotionId promotionId) {
      return db.values().stream()
          .filter(s -> promotionId.equals(s.promotionId()))
          .toList();
    }
  }

  private final InMemoryStudents repo = new InMemoryStudents();
  private final Clock clock = Clock.fixed(Instant.parse("2025-09-01T08:00:00Z"), ZoneOffset.UTC);
  private final EnrollStudentHandler handler = new EnrollStudentHandler(repo, clock);

  @Test
  void enrolls_a_student_with_deterministic_timestamp() {
    StudentView view = handler.handle(
        new EnrollStudentCommand("STU-2025-0001", "Ada", "Lovelace", "ada@example.com"));

    assertThat(view.status()).isEqualTo("ENROLLED");
    assertThat(view.enrolledAt()).isEqualTo(Instant.parse("2025-09-01T08:00:00Z"));
    assertThat(repo.db).hasSize(1);
  }

  @Test
  void rejects_an_invalid_student_number() {
    assertThatThrownBy(
        () -> handler.handle(
            new EnrollStudentCommand("BAD", "Ada", "Lovelace", "ada@example.com")))
                .isInstanceOf(InvalidStudentNumber.class);
  }
}
