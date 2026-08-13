package com.schoolmanagement.student.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.schoolmanagement.shared.AbstractIntegrationTest;
import com.schoolmanagement.shared.domain.EmailAddress;
import com.schoolmanagement.shared.domain.FullName;
import com.schoolmanagement.student.domain.Student;
import com.schoolmanagement.student.domain.StudentId;
import com.schoolmanagement.student.domain.StudentNumber;
import com.schoolmanagement.student.domain.StudentRepository;
import com.schoolmanagement.student.domain.exception.StudentNotFound;

@Transactional
class StudentRepositoryAdapterIT extends AbstractIntegrationTest {

  @Autowired
  private StudentRepository students;

  @Test
  void saves_and_retrieves_a_student() {
    Student student = Student.enroll(
        StudentId.generate(),
        new StudentNumber("STU-2025-0099"),
        new FullName("Grace", "Hopper"),
        new EmailAddress("grace@example.com"),
        Instant.parse("2025-09-01T08:00:00Z"));

    students.save(student);

    Student found = students.getById(student.id());

    assertThat(found.id()).isEqualTo(student.id());
    assertThat(found.number()).isEqualTo(student.number());
    assertThat(found.name()).isEqualTo(student.name());
    assertThat(found.email()).isEqualTo(student.email());
    assertThat(found.status()).isEqualTo(student.status());
    assertThat(found.enrolledAt()).isEqualTo(student.enrolledAt());
    assertThat(found.promotionId()).isNull();
  }

  @Test
  void throws_when_student_not_found() {
    assertThatThrownBy(() -> students.getById(StudentId.generate()))
        .isInstanceOf(StudentNotFound.class);
  }
}
