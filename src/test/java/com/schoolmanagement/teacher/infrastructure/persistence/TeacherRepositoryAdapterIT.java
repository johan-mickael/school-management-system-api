package com.schoolmanagement.teacher.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.schoolmanagement.shared.AbstractIntegrationTest;
import com.schoolmanagement.shared.domain.EmailAddress;
import com.schoolmanagement.shared.domain.FullName;
import com.schoolmanagement.teacher.domain.StaffNumber;
import com.schoolmanagement.teacher.domain.Teacher;
import com.schoolmanagement.teacher.domain.TeacherId;
import com.schoolmanagement.teacher.domain.TeacherRepository;
import com.schoolmanagement.teacher.domain.exception.TeacherNotFound;

@Transactional
class TeacherRepositoryAdapterIT extends AbstractIntegrationTest {

  @Autowired
  private TeacherRepository teachers;

  @Test
  void saves_and_retrieves_a_teacher() {
    Teacher teacher = Teacher.hire(
        TeacherId.generate(),
        new StaffNumber("TCH-2025-0099"),
        new FullName("Grace", "Hopper"),
        new EmailAddress("grace.teacher@example.com"),
        Instant.parse("2025-09-01T08:00:00Z"));

    teachers.save(teacher);

    Teacher found = teachers.getById(teacher.id());

    assertThat(found.id()).isEqualTo(teacher.id());
    assertThat(found.number()).isEqualTo(teacher.number());
    assertThat(found.name()).isEqualTo(teacher.name());
    assertThat(found.email()).isEqualTo(teacher.email());
    assertThat(found.status()).isEqualTo(teacher.status());
    assertThat(found.hiredAt()).isEqualTo(teacher.hiredAt());
  }

  @Test
  void throws_when_teacher_not_found() {
    assertThatThrownBy(() -> teachers.getById(TeacherId.generate()))
        .isInstanceOf(TeacherNotFound.class);
  }
}
