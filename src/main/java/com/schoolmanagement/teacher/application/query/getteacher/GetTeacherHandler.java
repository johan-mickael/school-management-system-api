package com.schoolmanagement.teacher.application.query.getteacher;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.schoolmanagement.teacher.application.view.TeacherView;
import com.schoolmanagement.teacher.domain.Teacher;
import com.schoolmanagement.teacher.domain.TeacherId;
import com.schoolmanagement.teacher.domain.TeacherRepository;

@Service
public class GetTeacherHandler {
  private final TeacherRepository teachers;

  public GetTeacherHandler(TeacherRepository teachers) {
    this.teachers = teachers;
  }

  @Transactional(readOnly = true)
  public TeacherView handle(GetTeacherQuery query) {
    Teacher foundTeacher = teachers.getById(TeacherId.of(query.teacherId()));

    return TeacherView.from(foundTeacher);
  }
}
