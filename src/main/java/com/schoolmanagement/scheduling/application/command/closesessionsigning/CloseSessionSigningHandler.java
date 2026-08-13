package com.schoolmanagement.scheduling.application.command.closesessionsigning;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.schoolmanagement.scheduling.application.view.SessionView;
import com.schoolmanagement.scheduling.domain.Session;
import com.schoolmanagement.scheduling.domain.SessionId;
import com.schoolmanagement.scheduling.domain.SessionRepository;

@Service
public class CloseSessionSigningHandler {
  private final SessionRepository sessions;

  public CloseSessionSigningHandler(SessionRepository sessions) {
    this.sessions = sessions;
  }

  @Transactional
  public SessionView handle(CloseSessionSigningCommand command) {
    Session session = sessions.getById(SessionId.of(command.sessionId()));

    session.closeSigning();
    sessions.save(session);

    return SessionView.from(session);
  }
}
