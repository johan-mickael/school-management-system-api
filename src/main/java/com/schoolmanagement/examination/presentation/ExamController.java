package com.schoolmanagement.examination.presentation;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.schoolmanagement.examination.application.command.closeexam.CloseExamCommand;
import com.schoolmanagement.examination.application.command.closeexam.CloseExamHandler;
import com.schoolmanagement.examination.application.command.openexam.OpenExamCommand;
import com.schoolmanagement.examination.application.command.openexam.OpenExamHandler;
import com.schoolmanagement.examination.application.command.scheduleexam.ScheduleExamCommand;
import com.schoolmanagement.examination.application.command.scheduleexam.ScheduleExamHandler;
import com.schoolmanagement.examination.application.query.getexam.GetExamHandler;
import com.schoolmanagement.examination.application.query.getexam.GetExamQuery;
import com.schoolmanagement.examination.application.query.listexams.ListExamsHandler;
import com.schoolmanagement.examination.application.query.listexams.ListExamsQuery;
import com.schoolmanagement.examination.application.view.ExamView;
import com.schoolmanagement.examination.presentation.dto.ExamResponse;
import com.schoolmanagement.examination.presentation.dto.ScheduleExamRequest;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/exams")
@Tag(name = "Exams")
public class ExamController {

  private final ScheduleExamHandler schedule;
  private final GetExamHandler get;
  private final ListExamsHandler list;
  private final OpenExamHandler open;
  private final CloseExamHandler close;

  public ExamController(
      ScheduleExamHandler schedule,
      GetExamHandler get,
      ListExamsHandler list,
      OpenExamHandler open,
      CloseExamHandler close) {
    this.schedule = schedule;
    this.get = get;
    this.list = list;
    this.open = open;
    this.close = close;
  }

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ExamResponse> schedule(@Valid @RequestBody ScheduleExamRequest request) {
    ScheduleExamCommand scheduleExamCommand = new ScheduleExamCommand(
        request.courseId(),
        request.promotionId(),
        request.start(),
        request.end());
    ExamView view = schedule.handle(scheduleExamCommand);

    return ResponseEntity
        .created(URI.create("/api/v1/exams/" + view.id()))
        .body(ExamResponse.from(view));
  }

  @GetMapping("/{id}")
  public ExamResponse getOne(@PathVariable String id) {
    return ExamResponse.from(get.handle(new GetExamQuery(id)));
  }

  @GetMapping
  public List<ExamResponse> getByPromotion(@RequestParam String promotionId) {
    return list.handle(new ListExamsQuery(promotionId)).stream()
        .map(ExamResponse::from)
        .toList();
  }

  @PostMapping("/{id}/open")
  @PreAuthorize("hasRole('ADMIN') or (hasRole('TEACHER') and @examAccessPolicy.canManage(#id, authentication))")
  public ExamResponse open(@P("id") @PathVariable String id) {
    return ExamResponse.from(open.handle(new OpenExamCommand(id)));
  }

  @PostMapping("/{id}/close")
  @PreAuthorize("hasRole('ADMIN') or (hasRole('TEACHER') and @examAccessPolicy.canManage(#id, authentication))")
  public ExamResponse close(@P("id") @PathVariable String id) {
    return ExamResponse.from(close.handle(new CloseExamCommand(id)));
  }
}
