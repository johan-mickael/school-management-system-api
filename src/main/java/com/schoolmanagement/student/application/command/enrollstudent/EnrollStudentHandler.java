package com.schoolmanagement.student.application.command.enrollstudent;

import java.time.Clock;
import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.schoolmanagement.student.application.view.StudentView;
import com.schoolmanagement.student.domain.EmailAddress;
import com.schoolmanagement.student.domain.FullName;
import com.schoolmanagement.student.domain.Student;
import com.schoolmanagement.student.domain.StudentId;
import com.schoolmanagement.student.domain.StudentNumber;
import com.schoolmanagement.student.domain.StudentRepository;

@Service
public class EnrollStudentHandler {
  private final StudentRepository students;
  private final Clock clock;

  public EnrollStudentHandler(StudentRepository students, Clock clock) {
    this.students = students;
    this.clock = clock;
  }

  @Transactional
  public StudentView handle(EnrollStudentCommand command) {
    Student student = Student.enroll(
        StudentId.generate(),
        new StudentNumber(command.studentNumber()),
        new FullName(command.firstName(), command.lastName()),
        new EmailAddress(command.email()),
        Instant.now(clock));

    students.save(student);

    return StudentView.from(student);
  }
}
