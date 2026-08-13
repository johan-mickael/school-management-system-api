package com.schoolmanagement.teacher.application;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;

import com.schoolmanagement.teacher.application.command.archiveteacher.ArchiveTeacherCommand;
import com.schoolmanagement.teacher.application.command.archiveteacher.ArchiveTeacherHandler;
import com.schoolmanagement.teacher.application.command.hireteacher.HireTeacherCommand;
import com.schoolmanagement.teacher.application.command.hireteacher.HireTeacherHandler;
import com.schoolmanagement.teacher.application.view.TeacherView;
import com.schoolmanagement.teacher.domain.Teacher;
import com.schoolmanagement.teacher.domain.TeacherId;
import com.schoolmanagement.teacher.domain.TeacherRepository;
import com.schoolmanagement.teacher.domain.exception.InvalidStaffNumber;
import com.schoolmanagement.teacher.domain.exception.TeacherAlreadyArchived;
import com.schoolmanagement.teacher.domain.exception.TeacherNotFound;

class HireTeacherHandlerTest {
  static class InMemoryTeachers implements TeacherRepository {
    final List<Teacher> db = new ArrayList<>();

    public void save(Teacher t) {
      db.removeIf(existing -> existing.id().equals(t.id()));
      db.add(t);
    }

    public Teacher getById(TeacherId id) {
      return db.stream()
          .filter(t -> t.id().equals(id))
          .findFirst()
          .orElseThrow(() -> new TeacherNotFound(id));
    }

    public List<Teacher> findAll() {
      return List.copyOf(db);
    }
  }

  private final InMemoryTeachers repo = new InMemoryTeachers();
  private final Clock clock = Clock.fixed(Instant.parse("2025-09-01T08:00:00Z"), ZoneOffset.UTC);
  private final HireTeacherHandler hireHandler = new HireTeacherHandler(repo, clock);
  private final ArchiveTeacherHandler archiveHandler = new ArchiveTeacherHandler(repo);

  @Test
  void hires_a_teacher_with_deterministic_timestamp() {
    TeacherView view = hireHandler.handle(
        new HireTeacherCommand("TCH-2025-0001", "Ada", "Lovelace", "ada@example.com"));

    assertThat(view.status()).isEqualTo("ACTIVE");
    assertThat(view.hiredAt()).isEqualTo(Instant.parse("2025-09-01T08:00:00Z"));
    assertThat(repo.db).hasSize(1);
  }

  @Test
  void rejects_an_invalid_staff_number() {
    assertThatThrownBy(
        () -> hireHandler.handle(
            new HireTeacherCommand("BAD", "Ada", "Lovelace", "ada@example.com")))
                .isInstanceOf(InvalidStaffNumber.class);
  }

  @Test
  void archives_an_active_teacher() {
    TeacherView hired = hireHandler.handle(
        new HireTeacherCommand("TCH-2025-0001", "Ada", "Lovelace", "ada@example.com"));

    TeacherView archived = archiveHandler.handle(new ArchiveTeacherCommand(hired.id()));

    assertThat(archived.status()).isEqualTo("ARCHIVED");
  }

  @Test
  void rejects_archiving_an_already_archived_teacher() {
    TeacherView hired = hireHandler.handle(
        new HireTeacherCommand("TCH-2025-0001", "Ada", "Lovelace", "ada@example.com"));
    archiveHandler.handle(new ArchiveTeacherCommand(hired.id()));

    assertThatThrownBy(() -> archiveHandler.handle(new ArchiveTeacherCommand(hired.id())))
        .isInstanceOf(TeacherAlreadyArchived.class);
  }
}
