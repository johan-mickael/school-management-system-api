package com.schoolmanagement.student.application.query.getstudent;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.schoolmanagement.student.application.view.StudentView;
import com.schoolmanagement.student.domain.Student;
import com.schoolmanagement.student.domain.StudentId;
import com.schoolmanagement.student.domain.StudentRepository;

@Service
public class GetStudentHandler {
  private final StudentRepository students;

  public GetStudentHandler(StudentRepository students) {
    this.students = students;
  }

  @Transactional(readOnly = true)
  public StudentView handle(GetStudentQuery query) {
    Student foundStudent = students.getById(StudentId.of(query.studentId()));

    return StudentView.from(foundStudent);
  }
}
