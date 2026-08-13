package com.schoolmanagement.scheduling.infrastructure.security;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.schoolmanagement.iam.infrastructure.security.CurrentTeacherResolver;
import com.schoolmanagement.scheduling.domain.Session;
import com.schoolmanagement.scheduling.domain.SessionId;
import com.schoolmanagement.scheduling.domain.SessionRepository;

@Component
public class SessionAccessPolicy {
  private final SessionRepository sessions;
  private final CurrentTeacherResolver currentTeacher;

  public SessionAccessPolicy(SessionRepository sessions, CurrentTeacherResolver currentTeacher) {
    this.sessions = sessions;
    this.currentTeacher = currentTeacher;
  }

  public boolean canManage(String sessionId, Authentication authentication) {
    Session session = sessions.getById(SessionId.of(sessionId));
    return currentTeacher.resolve(authentication)
        .map(session::isTaughtBy)
        .orElse(false);
  }
}
