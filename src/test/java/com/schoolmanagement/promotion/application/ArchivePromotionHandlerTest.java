package com.schoolmanagement.promotion.application;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import com.schoolmanagement.promotion.application.command.archivepromotion.ArchivePromotionCommand;
import com.schoolmanagement.promotion.application.command.archivepromotion.ArchivePromotionHandler;
import com.schoolmanagement.promotion.application.view.PromotionView;
import com.schoolmanagement.promotion.domain.AcademicYear;
import com.schoolmanagement.promotion.domain.Capacity;
import com.schoolmanagement.promotion.domain.Promotion;
import com.schoolmanagement.promotion.domain.PromotionId;
import com.schoolmanagement.promotion.domain.PromotionName;
import com.schoolmanagement.promotion.domain.PromotionRepository;
import com.schoolmanagement.promotion.domain.exception.PromotionAlreadyArchived;
import com.schoolmanagement.promotion.domain.exception.PromotionNotFound;
import com.schoolmanagement.shared.domain.EmailAddress;
import com.schoolmanagement.shared.domain.FullName;
import com.schoolmanagement.student.domain.Student;
import com.schoolmanagement.student.domain.StudentId;
import com.schoolmanagement.student.domain.StudentNumber;
import com.schoolmanagement.student.domain.StudentRepository;
import com.schoolmanagement.student.domain.exception.StudentAlreadyArchived;
import com.schoolmanagement.student.domain.exception.StudentNotFound;

class ArchivePromotionHandlerTest {

  static class InMemoryPromotions implements PromotionRepository {
    final Map<UUID, Promotion> db = new HashMap<>();

    public void save(Promotion p) {
      db.put(p.id().value(), p);
    }

    public Promotion getById(PromotionId id) {
      Promotion p = db.get(id.value());
      if (p == null)
        throw new PromotionNotFound(id);
      return p;
    }

    public List<Promotion> findArchived() {
      return db.values().stream().filter(Promotion::isArchived).toList();
    }

    @Override
    public List<Promotion> findActive() {
      return db.values().stream().filter(p -> !p.isArchived()).toList();
    }
  }

  static class InMemoryStudents implements StudentRepository {
    final Map<UUID, Student> db = new HashMap<>();

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
      return db.values().stream().filter(s -> promotionId.equals(s.promotionId())).toList();
    }

    public List<Student> findActiveByPromotionId(PromotionId promotionId) {
      return findByPromotionId(promotionId).stream().filter(s -> !s.isArchived()).toList();
    }
  }

  private final InMemoryPromotions promotions = new InMemoryPromotions();
  private final InMemoryStudents students = new InMemoryStudents();
  private final ArchivePromotionHandler handler = new ArchivePromotionHandler(promotions, students);

  private Promotion aPromotion() {
    Promotion promotion = Promotion.create(
        PromotionId.generate(), new PromotionName("MSE 2025"), new AcademicYear("2025-2026"), new Capacity(30));
    promotions.save(promotion);
    return promotion;
  }

  private Student aStudent(String number, Promotion promotion) {
    Student student = Student.enroll(
        StudentId.generate(), new StudentNumber(number),
        new FullName("Ada", "Lovelace"), new EmailAddress(number.toLowerCase() + "@example.com"),
        Instant.parse("2025-09-01T00:00:00Z"));
    student.assignToPromotion(promotion.id());
    students.save(student);
    return student;
  }

  @Test
  void archives_a_promotion_and_cascades_to_its_students() {
    Promotion promotion = aPromotion();
    Student first = aStudent("STU-2025-0601", promotion);
    Student second = aStudent("STU-2025-0602", promotion);

    PromotionView view = handler.handle(new ArchivePromotionCommand(promotion.id().toString()));

    assertThat(view.status()).isEqualTo("ARCHIVED");
    assertThat(students.getById(first.id()).isArchived()).isTrue();
    assertThat(students.getById(second.id()).isArchived()).isTrue();
  }

  @Test
  void rejects_archiving_an_already_archived_promotion() {
    Promotion promotion = aPromotion();
    handler.handle(new ArchivePromotionCommand(promotion.id().toString()));

    assertThatThrownBy(() -> handler.handle(new ArchivePromotionCommand(promotion.id().toString())))
        .isInstanceOf(PromotionAlreadyArchived.class);
  }

  @Test
  void an_archived_promotion_rejects_new_student_assignment() {
    Promotion promotion = aPromotion();
    handler.handle(new ArchivePromotionCommand(promotion.id().toString()));
    Student student = Student.enroll(
        StudentId.generate(), new StudentNumber("STU-2025-0603"),
        new FullName("Grace", "Hopper"), new EmailAddress("grace@example.com"),
        Instant.parse("2025-09-01T00:00:00Z"));
    students.save(student);

    Promotion archived = promotions.getById(promotion.id());
    assertThatThrownBy(archived::admit).isInstanceOf(PromotionAlreadyArchived.class);
  }

  @Test
  void an_archived_student_rejects_reassignment() {
    Promotion promotion = aPromotion();
    Student student = aStudent("STU-2025-0604", promotion);
    handler.handle(new ArchivePromotionCommand(promotion.id().toString()));
    Promotion other = aPromotion();

    Student archivedStudent = students.getById(student.id());
    assertThatThrownBy(() -> archivedStudent.assignToPromotion(other.id()))
        .isInstanceOf(StudentAlreadyArchived.class);
  }
}
