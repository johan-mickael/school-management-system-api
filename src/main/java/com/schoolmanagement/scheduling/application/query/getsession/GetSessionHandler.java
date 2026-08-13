package com.schoolmanagement.scheduling.application.query.getsession;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.schoolmanagement.scheduling.application.view.SessionView;
import com.schoolmanagement.scheduling.domain.Session;
import com.schoolmanagement.scheduling.domain.SessionId;
import com.schoolmanagement.scheduling.domain.SessionRepository;

@Service
public class GetSessionHandler {
  private final SessionRepository sessions;

  public GetSessionHandler(SessionRepository sessions) {
    this.sessions = sessions;
  }

  @Transactional(readOnly = true)
  public SessionView handle(GetSessionQuery query) {
    Session foundSession = sessions.getById(SessionId.of(query.sessionId()));

    return SessionView.from(foundSession);
  }
}
