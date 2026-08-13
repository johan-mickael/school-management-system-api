package com.schoolmanagement.promotion.application;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;

import com.schoolmanagement.promotion.application.command.assignstudenttopromotion.AssignStudentToPromotionCommand;
import com.schoolmanagement.promotion.application.command.assignstudenttopromotion.AssignStudentToPromotionHandler;
import com.schoolmanagement.promotion.domain.AcademicYear;
import com.schoolmanagement.promotion.domain.Capacity;
import com.schoolmanagement.promotion.domain.Promotion;
import com.schoolmanagement.promotion.domain.PromotionId;
import com.schoolmanagement.promotion.domain.PromotionName;
import com.schoolmanagement.promotion.domain.PromotionRepository;
import com.schoolmanagement.promotion.domain.exception.PromotionFull;
import com.schoolmanagement.promotion.domain.exception.PromotionNotFound;
import com.schoolmanagement.student.domain.EmailAddress;
import com.schoolmanagement.student.domain.FullName;
import com.schoolmanagement.student.domain.Student;
import com.schoolmanagement.student.domain.StudentId;
import com.schoolmanagement.student.domain.StudentNumber;
import com.schoolmanagement.student.domain.StudentRepository;
import com.schoolmanagement.student.domain.exception.StudentAlreadyAssignedToPromotion;
import com.schoolmanagement.student.domain.exception.StudentNotFound;

class AssignStudentToPromotionHandlerTest {
  static class InMemoryPromotions implements PromotionRepository {
    final Map<java.util.UUID, Promotion> db = new HashMap<>();

    public void save(Promotion p) {
      db.put(p.id().value(), p);
    }

    public Promotion getById(PromotionId id) {
      Promotion p = db.get(id.value());
      if (p == null)
        throw new PromotionNotFound(id);
      return p;
    }
  }

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

  private final InMemoryPromotions promotions = new InMemoryPromotions();
  private final InMemoryStudents students = new InMemoryStudents();
  private final AssignStudentToPromotionHandler handler =
      new AssignStudentToPromotionHandler(promotions, students);

  private Student aStudent(String number, String email) {
    return Student.enroll(
        StudentId.generate(),
        new StudentNumber(number),
        new FullName("Ada", "Lovelace"),
        new EmailAddress(email),
        Instant.parse("2025-09-01T08:00:00Z"));
  }

  @Test
  void assigns_a_student_when_promotion_has_capacity() {
    Promotion promotion = Promotion.create(
        PromotionId.generate(), new PromotionName("MSE 2025"), new AcademicYear("2025-2026"), new Capacity(1));
    promotions.save(promotion);
    Student student = aStudent("STU-2025-0001", "ada@example.com");
    students.save(student);

    handler.handle(new AssignStudentToPromotionCommand(promotion.id().toString(), student.id().toString()));

    assertThat(students.getById(student.id()).promotionId()).isEqualTo(promotion.id());
    assertThat(promotions.getById(promotion.id()).occupancy()).isEqualTo(1);
  }

  @Test
  void rejects_assignment_when_promotion_is_full() {
    Promotion promotion = Promotion.create(
        PromotionId.generate(), new PromotionName("MSE 2025"), new AcademicYear("2025-2026"), new Capacity(1));
    promotions.save(promotion);
    Student first = aStudent("STU-2025-0001", "ada@example.com");
    Student second = aStudent("STU-2025-0002", "grace@example.com");
    students.save(first);
    students.save(second);

    handler.handle(new AssignStudentToPromotionCommand(promotion.id().toString(), first.id().toString()));

    assertThatThrownBy(
        () -> handler.handle(new AssignStudentToPromotionCommand(promotion.id().toString(), second.id().toString())))
            .isInstanceOf(PromotionFull.class);
  }

  @Test
  void rejects_assigning_a_student_twice() {
    Promotion promotion = Promotion.create(
        PromotionId.generate(), new PromotionName("MSE 2025"), new AcademicYear("2025-2026"), new Capacity(5));
    promotions.save(promotion);
    Student student = aStudent("STU-2025-0001", "ada@example.com");
    students.save(student);

    handler.handle(new AssignStudentToPromotionCommand(promotion.id().toString(), student.id().toString()));

    assertThatThrownBy(
        () -> handler.handle(new AssignStudentToPromotionCommand(promotion.id().toString(), student.id().toString())))
            .isInstanceOf(StudentAlreadyAssignedToPromotion.class);
  }
}
