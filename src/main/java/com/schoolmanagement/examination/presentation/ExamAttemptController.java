package com.schoolmanagement.examination.presentation;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.schoolmanagement.examination.application.command.recordintegrityevent.RecordIntegrityEventCommand;
import com.schoolmanagement.examination.application.command.recordintegrityevent.RecordIntegrityEventHandler;
import com.schoolmanagement.examination.application.command.startattempt.StartAttemptCommand;
import com.schoolmanagement.examination.application.command.startattempt.StartAttemptHandler;
import com.schoolmanagement.examination.application.command.submitattempt.SubmitAttemptCommand;
import com.schoolmanagement.examination.application.command.submitattempt.SubmitAttemptHandler;
import com.schoolmanagement.examination.application.query.getattempt.GetAttemptHandler;
import com.schoolmanagement.examination.application.query.getattempt.GetAttemptQuery;
import com.schoolmanagement.examination.application.query.listexamattempts.ListExamAttemptsHandler;
import com.schoolmanagement.examination.application.query.listexamattempts.ListExamAttemptsQuery;
import com.schoolmanagement.examination.presentation.dto.ExamAttemptResponse;
import com.schoolmanagement.examination.presentation.dto.RecordIntegrityEventRequest;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@Tag(name = "Exam attempts")
public class ExamAttemptController {

  private final StartAttemptHandler start;
  private final RecordIntegrityEventHandler recordEvent;
  private final SubmitAttemptHandler submit;
  private final GetAttemptHandler get;
  private final ListExamAttemptsHandler listByExam;

  public ExamAttemptController(
      StartAttemptHandler start,
      RecordIntegrityEventHandler recordEvent,
      SubmitAttemptHandler submit,
      GetAttemptHandler get,
      ListExamAttemptsHandler listByExam) {
    this.start = start;
    this.recordEvent = recordEvent;
    this.submit = submit;
    this.get = get;
    this.listByExam = listByExam;
  }

  @PostMapping("/api/v1/exams/{examId}/attempts/start")
  public ExamAttemptResponse start(@PathVariable String examId, Authentication authentication) {
    return ExamAttemptResponse.from(start.handle(new StartAttemptCommand(examId, authentication.getName())));
  }

  @PostMapping("/api/v1/attempts/{attemptId}/events")
  public ExamAttemptResponse recordEvent(
      @PathVariable String attemptId,
      @Valid @RequestBody RecordIntegrityEventRequest request) {
    return ExamAttemptResponse.from(
        recordEvent.handle(new RecordIntegrityEventCommand(attemptId, request.eventType())));
  }

  @PostMapping("/api/v1/attempts/{attemptId}/submit")
  public ExamAttemptResponse submit(@PathVariable String attemptId) {
    return ExamAttemptResponse.from(submit.handle(new SubmitAttemptCommand(attemptId)));
  }

  @GetMapping("/api/v1/attempts/{attemptId}")
  public ExamAttemptResponse getOne(@PathVariable String attemptId) {
    return ExamAttemptResponse.from(get.handle(new GetAttemptQuery(attemptId)));
  }

  @GetMapping("/api/v1/exams/{examId}/attempts")
  public List<ExamAttemptResponse> getByExam(
      @PathVariable String examId,
      @RequestParam(required = false, defaultValue = "false") boolean flagged) {
    return listByExam.handle(new ListExamAttemptsQuery(examId, flagged)).stream()
        .map(ExamAttemptResponse::from)
        .toList();
  }
}
