package com.schoolmanagement.promotion.application;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.schoolmanagement.promotion.application.command.archivepromotion.ArchivePromotionCommand;
import com.schoolmanagement.promotion.application.command.archivepromotion.ArchivePromotionHandler;
import com.schoolmanagement.promotion.application.query.getarchivedpromotion.GetArchivedPromotionHandler;
import com.schoolmanagement.promotion.application.query.getarchivedpromotion.GetArchivedPromotionQuery;
import com.schoolmanagement.promotion.application.query.listarchivedpromotions.ListArchivedPromotionsHandler;
import com.schoolmanagement.promotion.application.query.listarchivedpromotions.ListArchivedPromotionsQuery;
import com.schoolmanagement.promotion.application.query.listpromotionstudents.ListPromotionStudentsHandler;
import com.schoolmanagement.promotion.application.query.listpromotionstudents.ListPromotionStudentsQuery;
import com.schoolmanagement.promotion.application.view.PromotionView;
import com.schoolmanagement.promotion.domain.AcademicYear;
import com.schoolmanagement.promotion.domain.Capacity;
import com.schoolmanagement.promotion.domain.Promotion;
import com.schoolmanagement.promotion.domain.PromotionId;
import com.schoolmanagement.promotion.domain.PromotionName;
import com.schoolmanagement.promotion.domain.PromotionRepository;
import com.schoolmanagement.promotion.domain.exception.PromotionAlreadyArchived;
import com.schoolmanagement.promotion.domain.exception.PromotionNotFound;
import com.schoolmanagement.shared.AbstractIntegrationTest;
import com.schoolmanagement.shared.domain.EmailAddress;
import com.schoolmanagement.shared.domain.FullName;
import com.schoolmanagement.student.domain.Student;
import com.schoolmanagement.student.domain.StudentNumber;
import com.schoolmanagement.student.domain.StudentRepository;

@Transactional
class PromotionArchiveIT extends AbstractIntegrationTest {

  @Autowired
  private ArchivePromotionHandler archivePromotion;

  @Autowired
  private ListArchivedPromotionsHandler listArchived;

  @Autowired
  private GetArchivedPromotionHandler getArchived;

  @Autowired
  private ListPromotionStudentsHandler listStudents;

  @Autowired
  private PromotionRepository promotions;

  @Autowired
  private StudentRepository students;

  private Promotion aPromotion(String name) {
    Promotion promotion = Promotion.create(
        PromotionId.generate(), new PromotionName(name), new AcademicYear("2025-2026"), new Capacity(30));
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

  @Test
  void archiving_a_promotion_cascades_to_its_students_and_is_excluded_from_the_active_roster() {
    Promotion promotion = aPromotion("MSE 2025");
    Student first = aStudent("STU-2025-0701", promotion);
    Student second = aStudent("STU-2025-0702", promotion);

    archivePromotion.handle(new ArchivePromotionCommand(promotion.id().toString()));

    assertThat(students.getById(first.id()).isArchived()).isTrue();
    assertThat(students.getById(second.id()).isArchived()).isTrue();
    assertThat(listStudents.handle(new ListPromotionStudentsQuery(promotion.id().toString()))).isEmpty();
  }

  @Test
  void archived_promotions_are_listed_and_gettable_via_the_archive_queries() {
    Promotion active = aPromotion("MSE 2025");
    Promotion archived = aPromotion("MSE 2024");
    archivePromotion.handle(new ArchivePromotionCommand(archived.id().toString()));

    assertThat(listArchived.handle(new ListArchivedPromotionsQuery()))
        .extracting(PromotionView::id)
        .containsExactly(archived.id().toString());

    PromotionView view = getArchived.handle(new GetArchivedPromotionQuery(archived.id().toString()));
    assertThat(view.status()).isEqualTo("ARCHIVED");

    assertThatThrownBy(() -> getArchived.handle(new GetArchivedPromotionQuery(active.id().toString())))
        .isInstanceOf(PromotionNotFound.class);
  }

  @Test
  void rejects_archiving_an_already_archived_promotion() {
    Promotion promotion = aPromotion("MSE 2025");
    archivePromotion.handle(new ArchivePromotionCommand(promotion.id().toString()));

    assertThatThrownBy(() -> archivePromotion.handle(new ArchivePromotionCommand(promotion.id().toString())))
        .isInstanceOf(PromotionAlreadyArchived.class);
  }
}
