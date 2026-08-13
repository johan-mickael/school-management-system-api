package com.schoolmanagement.teacher.application.command.archiveteacher;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.schoolmanagement.teacher.application.view.TeacherView;
import com.schoolmanagement.teacher.domain.Teacher;
import com.schoolmanagement.teacher.domain.TeacherId;
import com.schoolmanagement.teacher.domain.TeacherRepository;

@Service
public class ArchiveTeacherHandler {
  private final TeacherRepository teachers;

  public ArchiveTeacherHandler(TeacherRepository teachers) {
    this.teachers = teachers;
  }

  @Transactional
  public TeacherView handle(ArchiveTeacherCommand command) {
    Teacher teacher = teachers.getById(TeacherId.of(command.teacherId()));

    teacher.archive();
    teachers.save(teacher);

    return TeacherView.from(teacher);
  }
}
