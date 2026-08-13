package com.schoolmanagement.scheduling.presentation;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.schoolmanagement.scheduling.application.command.cancelsession.CancelSessionCommand;
import com.schoolmanagement.scheduling.application.command.cancelsession.CancelSessionHandler;
import com.schoolmanagement.scheduling.application.command.closesessionsigning.CloseSessionSigningCommand;
import com.schoolmanagement.scheduling.application.command.closesessionsigning.CloseSessionSigningHandler;
import com.schoolmanagement.scheduling.application.command.opensessionsigning.OpenSessionSigningCommand;
import com.schoolmanagement.scheduling.application.command.opensessionsigning.OpenSessionSigningHandler;
import com.schoolmanagement.scheduling.application.command.schedulesession.ScheduleSessionCommand;
import com.schoolmanagement.scheduling.application.command.schedulesession.ScheduleSessionHandler;
import com.schoolmanagement.scheduling.application.query.getsession.GetSessionHandler;
import com.schoolmanagement.scheduling.application.query.getsession.GetSessionQuery;
import com.schoolmanagement.scheduling.application.query.listsessions.ListSessionsHandler;
import com.schoolmanagement.scheduling.application.query.listsessions.ListSessionsQuery;
import com.schoolmanagement.scheduling.application.view.SessionView;
import com.schoolmanagement.scheduling.presentation.dto.ScheduleSessionRequest;
import com.schoolmanagement.scheduling.presentation.dto.SessionResponse;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/sessions")
@Tag(name = "Sessions")
public class SessionController {

  private final ScheduleSessionHandler schedule;
  private final GetSessionHandler get;
  private final ListSessionsHandler list;
  private final OpenSessionSigningHandler open;
  private final CloseSessionSigningHandler close;
  private final CancelSessionHandler cancel;

  public SessionController(
      ScheduleSessionHandler schedule,
      GetSessionHandler get,
      ListSessionsHandler list,
      OpenSessionSigningHandler open,
      CloseSessionSigningHandler close,
      CancelSessionHandler cancel) {
    this.schedule = schedule;
    this.get = get;
    this.list = list;
    this.open = open;
    this.close = close;
    this.cancel = cancel;
  }

  @PostMapping
  public ResponseEntity<SessionResponse> schedule(@Valid @RequestBody ScheduleSessionRequest request) {
    ScheduleSessionCommand scheduleSessionCommand = new ScheduleSessionCommand(
        request.courseId(),
        request.promotionId(),
        request.teacherId(),
        request.start(),
        request.end(),
        request.gracePeriodSeconds());
    SessionView view = schedule.handle(scheduleSessionCommand);

    return ResponseEntity
        .created(URI.create("/api/v1/sessions/" + view.id()))
        .body(SessionResponse.from(view));
  }

  @GetMapping("/{id}")
  public SessionResponse getOne(@PathVariable String id) {
    return SessionResponse.from(get.handle(new GetSessionQuery(id)));
  }

  @GetMapping
  public List<SessionResponse> getByPromotion(
      @RequestParam String promotionId,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
    return list.handle(new ListSessionsQuery(promotionId, date)).stream()
        .map(SessionResponse::from)
        .toList();
  }

  @PostMapping("/{id}/open")
  public SessionResponse openSigning(@PathVariable String id) {
    return SessionResponse.from(open.handle(new OpenSessionSigningCommand(id)));
  }

  @PostMapping("/{id}/close")
  public SessionResponse closeSigning(@PathVariable String id) {
    return SessionResponse.from(close.handle(new CloseSessionSigningCommand(id)));
  }

  @PostMapping("/{id}/cancel")
  public SessionResponse cancel(@PathVariable String id) {
    return SessionResponse.from(cancel.handle(new CancelSessionCommand(id)));
  }
}
