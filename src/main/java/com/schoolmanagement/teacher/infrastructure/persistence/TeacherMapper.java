package com.schoolmanagement.teacher.infrastructure.persistence;

import org.springframework.stereotype.Component;

import com.schoolmanagement.shared.domain.EmailAddress;
import com.schoolmanagement.shared.domain.FullName;
import com.schoolmanagement.teacher.domain.StaffNumber;
import com.schoolmanagement.teacher.domain.Teacher;
import com.schoolmanagement.teacher.domain.TeacherId;

@Component
public class TeacherMapper {

  public TeacherEntity toEntity(Teacher t) {
    return new TeacherEntity(
        t.id().value(),
        t.number().value(),
        t.name().firstName(),
        t.name().lastName(),
        t.email().value(),
        t.status(),
        t.hiredAt());
  }

  public Teacher toDomain(TeacherEntity e) {
    return Teacher.reconstitute(
        new TeacherId(e.getId()),
        new StaffNumber(e.getNumber()),
        new FullName(e.getFirstName(), e.getLastName()),
        new EmailAddress(e.getEmail()),
        e.getStatus(),
        e.getHiredAt());
  }
}
