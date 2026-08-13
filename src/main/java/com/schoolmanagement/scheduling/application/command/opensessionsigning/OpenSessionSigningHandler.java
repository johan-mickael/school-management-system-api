package com.schoolmanagement.scheduling.application.command.opensessionsigning;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.schoolmanagement.scheduling.application.view.SessionView;
import com.schoolmanagement.scheduling.domain.Session;
import com.schoolmanagement.scheduling.domain.SessionId;
import com.schoolmanagement.scheduling.domain.SessionRepository;

@Service
public class OpenSessionSigningHandler {
  private final SessionRepository sessions;

  public OpenSessionSigningHandler(SessionRepository sessions) {
    this.sessions = sessions;
  }

  @Transactional
  public SessionView handle(OpenSessionSigningCommand command) {
    Session session = sessions.getById(SessionId.of(command.sessionId()));

    session.openSigning();
    sessions.save(session);

    return SessionView.from(session);
  }
}
