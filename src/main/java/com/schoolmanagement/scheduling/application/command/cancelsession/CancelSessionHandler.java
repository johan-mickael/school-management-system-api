package com.schoolmanagement.scheduling.application.command.cancelsession;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.schoolmanagement.scheduling.application.view.SessionView;
import com.schoolmanagement.scheduling.domain.Session;
import com.schoolmanagement.scheduling.domain.SessionId;
import com.schoolmanagement.scheduling.domain.SessionRepository;

@Service
public class CancelSessionHandler {
  private final SessionRepository sessions;

  public CancelSessionHandler(SessionRepository sessions) {
    this.sessions = sessions;
  }

  @Transactional
  public SessionView handle(CancelSessionCommand command) {
    Session session = sessions.getById(SessionId.of(command.sessionId()));

    session.cancel();
    sessions.save(session);

    return SessionView.from(session);
  }
}
