package com.schoolmanagement.teacher.application.command.hireteacher;

import java.time.Clock;
import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.schoolmanagement.shared.domain.EmailAddress;
import com.schoolmanagement.shared.domain.FullName;
import com.schoolmanagement.teacher.application.view.TeacherView;
import com.schoolmanagement.teacher.domain.StaffNumber;
import com.schoolmanagement.teacher.domain.Teacher;
import com.schoolmanagement.teacher.domain.TeacherId;
import com.schoolmanagement.teacher.domain.TeacherRepository;

@Service
public class HireTeacherHandler {
  private final TeacherRepository teachers;
  private final Clock clock;

  public HireTeacherHandler(TeacherRepository teachers, Clock clock) {
    this.teachers = teachers;
    this.clock = clock;
  }

  @Transactional
  public TeacherView handle(HireTeacherCommand command) {
    Teacher teacher = Teacher.hire(
        TeacherId.generate(),
        new StaffNumber(command.staffNumber()),
        new FullName(command.firstName(), command.lastName()),
        new EmailAddress(command.email()),
        Instant.now(clock));

    teachers.save(teacher);

    return TeacherView.from(teacher);
  }
}
