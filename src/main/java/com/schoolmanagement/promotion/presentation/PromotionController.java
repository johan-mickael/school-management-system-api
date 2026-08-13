package com.schoolmanagement.promotion.presentation;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.schoolmanagement.promotion.application.command.archivepromotion.ArchivePromotionCommand;
import com.schoolmanagement.promotion.application.command.archivepromotion.ArchivePromotionHandler;
import com.schoolmanagement.promotion.application.command.assignstudenttopromotion.AssignStudentToPromotionCommand;
import com.schoolmanagement.promotion.application.command.assignstudenttopromotion.AssignStudentToPromotionHandler;
import com.schoolmanagement.promotion.application.command.createpromotion.CreatePromotionCommand;
import com.schoolmanagement.promotion.application.command.createpromotion.CreatePromotionHandler;
import com.schoolmanagement.promotion.application.query.getpromotion.GetPromotionHandler;
import com.schoolmanagement.promotion.application.query.getpromotion.GetPromotionQuery;
import com.schoolmanagement.promotion.application.query.listpromotionstudents.ListPromotionStudentsHandler;
import com.schoolmanagement.promotion.application.query.listpromotionstudents.ListPromotionStudentsQuery;
import com.schoolmanagement.promotion.application.view.PromotionView;
import com.schoolmanagement.promotion.presentation.dto.AssignStudentToPromotionRequest;
import com.schoolmanagement.promotion.presentation.dto.CreatePromotionRequest;
import com.schoolmanagement.promotion.presentation.dto.PromotionResponse;
import com.schoolmanagement.student.application.view.StudentView;
import com.schoolmanagement.student.presentation.dto.StudentResponse;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/promotions")
@Tag(name = "Promotions")
public class PromotionController {

  private final CreatePromotionHandler create;
  private final GetPromotionHandler get;
  private final ListPromotionStudentsHandler listStudents;
  private final AssignStudentToPromotionHandler assignStudent;
  private final ArchivePromotionHandler archive;

  public PromotionController(
      CreatePromotionHandler create,
      GetPromotionHandler get,
      ListPromotionStudentsHandler listStudents,
      AssignStudentToPromotionHandler assignStudent,
      ArchivePromotionHandler archive) {
    this.create = create;
    this.get = get;
    this.listStudents = listStudents;
    this.assignStudent = assignStudent;
    this.archive = archive;
  }

  @PostMapping
  public ResponseEntity<PromotionResponse> create(@Valid @RequestBody CreatePromotionRequest request) {
    CreatePromotionCommand createPromotionCommand = new CreatePromotionCommand(
        request.name(),
        request.academicYear(),
        request.capacity());
    PromotionView view = create.handle(createPromotionCommand);

    return ResponseEntity
        .created(URI.create("/api/v1/promotions/" + view.id()))
        .body(PromotionResponse.from(view));
  }

  @GetMapping("/{id}")
  public PromotionResponse getOne(@PathVariable String id) {
    return PromotionResponse.from(get.handle(new GetPromotionQuery(id)));
  }

  @GetMapping("/{id}/students")
  public List<StudentResponse> getStudents(@PathVariable String id) {
    List<StudentView> views = listStudents.handle(new ListPromotionStudentsQuery(id));
    return views.stream().map(StudentResponse::from).toList();
  }

  @PostMapping("/{id}/students")
  public StudentResponse assignStudent(
      @PathVariable String id,
      @Valid @RequestBody AssignStudentToPromotionRequest request) {
    AssignStudentToPromotionCommand command = new AssignStudentToPromotionCommand(id, request.studentId());
    return StudentResponse.from(assignStudent.handle(command));
  }

  @PostMapping("/{id}/archive")
  public PromotionResponse archive(@PathVariable String id) {
    return PromotionResponse.from(archive.handle(new ArchivePromotionCommand(id)));
  }
}
