package com.schoolmanagement.teacher.application.query.listteachers;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.schoolmanagement.teacher.application.view.TeacherView;
import com.schoolmanagement.teacher.domain.TeacherRepository;

@Service
public class ListTeachersHandler {
  private final TeacherRepository teachers;

  public ListTeachersHandler(TeacherRepository teachers) {
    this.teachers = teachers;
  }

  @Transactional(readOnly = true)
  public List<TeacherView> handle(ListTeachersQuery query) {
    return teachers.findAll().stream()
        .map(TeacherView::from)
        .toList();
  }
}
