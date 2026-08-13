package com.schoolmanagement.grading.application.query.getstudentgrades;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.schoolmanagement.grading.application.view.GradeView;
import com.schoolmanagement.grading.domain.GradeRepository;
import com.schoolmanagement.student.domain.StudentId;

@Service
public class GetStudentGradesHandler {
  private final GradeRepository grades;

  public GetStudentGradesHandler(GradeRepository grades) {
    this.grades = grades;
  }

  @Transactional(readOnly = true)
  public List<GradeView> handle(GetStudentGradesQuery query) {
    return grades.findByStudentId(StudentId.of(query.studentId())).stream()
        .map(GradeView::from)
        .toList();
  }
}
