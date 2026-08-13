package com.schoolmanagement.teacher.presentation;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.schoolmanagement.teacher.application.command.archiveteacher.ArchiveTeacherCommand;
import com.schoolmanagement.teacher.application.command.archiveteacher.ArchiveTeacherHandler;
import com.schoolmanagement.teacher.application.command.hireteacher.HireTeacherCommand;
import com.schoolmanagement.teacher.application.command.hireteacher.HireTeacherHandler;
import com.schoolmanagement.teacher.application.query.getteacher.GetTeacherHandler;
import com.schoolmanagement.teacher.application.query.getteacher.GetTeacherQuery;
import com.schoolmanagement.teacher.application.query.listteachers.ListTeachersHandler;
import com.schoolmanagement.teacher.application.query.listteachers.ListTeachersQuery;
import com.schoolmanagement.teacher.application.view.TeacherView;
import com.schoolmanagement.teacher.presentation.dto.HireTeacherRequest;
import com.schoolmanagement.teacher.presentation.dto.TeacherResponse;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/teachers")
@Tag(name = "Teachers")
public class TeacherController {

  private final HireTeacherHandler hire;
  private final GetTeacherHandler get;
  private final ListTeachersHandler list;
  private final ArchiveTeacherHandler archive;

  public TeacherController(
      HireTeacherHandler hire,
      GetTeacherHandler get,
      ListTeachersHandler list,
      ArchiveTeacherHandler archive) {
    this.hire = hire;
    this.get = get;
    this.list = list;
    this.archive = archive;
  }

  @PostMapping
  public ResponseEntity<TeacherResponse> hire(@Valid @RequestBody HireTeacherRequest request) {
    HireTeacherCommand hireTeacherCommand = new HireTeacherCommand(
        request.staffNumber(),
        request.firstName(),
        request.lastName(),
        request.email());
    TeacherView view = hire.handle(hireTeacherCommand);

    return ResponseEntity
        .created(URI.create("/api/v1/teachers/" + view.id()))
        .body(TeacherResponse.from(view));
  }

  @GetMapping("/{id}")
  public TeacherResponse getOne(@PathVariable String id) {
    return TeacherResponse.from(get.handle(new GetTeacherQuery(id)));
  }

  @GetMapping
  public List<TeacherResponse> getAll() {
    return list.handle(new ListTeachersQuery()).stream()
        .map(TeacherResponse::from)
        .toList();
  }

  @PostMapping("/{id}/archive")
  public TeacherResponse archiveTeacher(@PathVariable String id) {
    return TeacherResponse.from(archive.handle(new ArchiveTeacherCommand(id)));
  }
}
