package com.schoolmanagement.student.presentation;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.schoolmanagement.student.application.command.archivestudent.ArchiveStudentCommand;
import com.schoolmanagement.student.application.command.archivestudent.ArchiveStudentHandler;
import com.schoolmanagement.student.application.command.enrollstudent.EnrollStudentCommand;
import com.schoolmanagement.student.application.command.enrollstudent.EnrollStudentHandler;
import com.schoolmanagement.student.application.query.getstudent.GetStudentHandler;
import com.schoolmanagement.student.application.query.getstudent.GetStudentQuery;
import com.schoolmanagement.student.application.view.StudentView;
import com.schoolmanagement.student.presentation.dto.EnrollStudentRequest;
import com.schoolmanagement.student.presentation.dto.StudentResponse;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/students")
@Tag(name = "Students")
public class StudentController {

  private final EnrollStudentHandler enroll;
  private final GetStudentHandler get;
  private final ArchiveStudentHandler archive;

  public StudentController(
      EnrollStudentHandler enroll,
      GetStudentHandler get,
      ArchiveStudentHandler archive) {
    this.enroll = enroll;
    this.get = get;
    this.archive = archive;
  }

  @PostMapping
  public ResponseEntity<StudentResponse> enroll(@Valid @RequestBody EnrollStudentRequest request) {
    EnrollStudentCommand enrollStudentCommand = new EnrollStudentCommand(
        request.studentNumber(),
        request.firstName(),
        request.lastName(),
        request.email());
    StudentView view = enroll.handle(enrollStudentCommand);

    return ResponseEntity
        .created(URI.create("/api/v1/students/" + view.id()))
        .body(StudentResponse.from(view));
  }

  @GetMapping("/{id}")
  public StudentResponse getOne(@PathVariable String id) {
    return StudentResponse.from(get.handle(new GetStudentQuery(id)));
  }

  @PostMapping("/{id}/archive")
  public StudentResponse archiveStudent(@PathVariable String id) {
    return StudentResponse.from(archive.handle(new ArchiveStudentCommand(id)));
  }
}
