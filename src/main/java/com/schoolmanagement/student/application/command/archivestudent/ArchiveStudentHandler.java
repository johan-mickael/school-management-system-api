package com.schoolmanagement.student.application.command.archivestudent;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.schoolmanagement.student.application.view.StudentView;
import com.schoolmanagement.student.domain.Student;
import com.schoolmanagement.student.domain.StudentId;
import com.schoolmanagement.student.domain.StudentRepository;

@Service
public class ArchiveStudentHandler {
  private final StudentRepository students;

  public ArchiveStudentHandler(StudentRepository students) {
    this.students = students;
  }

  @Transactional
  public StudentView handle(ArchiveStudentCommand command) {
    Student student = students.getById(StudentId.of(command.studentId()));

    student.archive();
    students.save(student);

    return StudentView.from(student);
  }
}
